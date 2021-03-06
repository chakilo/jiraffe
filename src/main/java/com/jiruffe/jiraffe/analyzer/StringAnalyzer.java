/*
 *    Copyright 2018 Jiruffe
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.jiruffe.jiraffe.analyzer;

import com.jiruffe.jiraffe.model.JSONElement;
import com.jiruffe.jiraffe.util.CharacterUtil;
import com.jiruffe.jiraffe.util.StringUtil;
import com.jiruffe.jiraffe.util.TypeUtil;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

/**
 * JSON {@link String} &lt;=&gt; {@link JSONElement} conversion.
 *
 * @author Jiruffe
 * 2018.10.25
 */
public abstract class StringAnalyzer {

    /**
     * JSON {@link String} =&gt; {@link JSONElement}.
     *
     * @param json the JSON {@link String}.
     * @return the {@link JSONElement} converted.
     */
    public static JSONElement analyze(String json) {

        if (null == json) {
            return JSONElement.theVoid();
        }

        // ignore spaces
        json = json.trim();

        // not map or list, return a value
        if (!json.startsWith("{") && !json.startsWith("[")) {
            boolean force_set_string = false;
            if ((json.startsWith("\"") && json.endsWith("\"")) || (json.startsWith("'") && json.endsWith("'"))) {
                force_set_string = true;
                json = json.substring(1, json.length() - 1);
            }
            return parseValue(json, force_set_string);
        }

        // stack to store upper element
        Queue<JSONElement> bases = Collections.asLifoQueue(new ArrayDeque<>());
        // stack to store keys
        Queue<String> keys = Collections.asLifoQueue(new ArrayDeque<>());
        // value read
        StringBuilder sb = new StringBuilder();
        // reader
        StringReader sr = new StringReader(json);
        char c;
        int ci = 0;
        // should set value as string
        boolean force_set_string = false;
        // the last token
        char last_token = CharacterUtil.NUL;

        // traversal of json string
        while (true) {

            try {
                if ((ci = sr.read()) == CharacterUtil.EOF) break;
            } catch (IOException ignored) {

            }

            c = (char) ci;

            if (CharacterUtil.isQuote(last_token)) {
                if (last_token == c) {
                    last_token = CharacterUtil.NUL;
                } else {
                    sb.append(c);
                }
                continue;
            }

            switch (c) {

                case '"':
                case '\'':
                    force_set_string = true;
                    last_token = c;
                    break;

                case '{':
                    bases.offer(JSONElement.newMap());
                    last_token = c;
                    break;

                case '[':
                    bases.offer(JSONElement.newList());
                    last_token = c;
                    break;

                case ':':
                    // new key
                    keys.offer(StringUtil.unescape(sb.toString()));
                    StringUtil.clear(sb);
                    last_token = c;
                    break;

                case ',':
                    if (StringUtil.isNotEmpty(sb) || force_set_string) {
                        // set the value to this element
                        JSONElement self = bases.peek();
                        if (self.isList()) {
                            self.offer(parseValue(sb.toString(), force_set_string));
                        } else if (self.isMap()) {
                            self.offer(keys.poll(), parseValue(sb.toString(), force_set_string));
                        }
                        StringUtil.clear(sb);
                        force_set_string = false;
                    } else if (!CharacterUtil.isRightBrackets(last_token) && !bases.isEmpty()) {
                        // set void
                        JSONElement self = bases.peek();
                        if (self.isList()) {
                            self.offer(JSONElement.theVoid());
                        } else if (self.isMap()) {
                            self.offer(keys.poll(), JSONElement.theVoid());
                        }
                    }
                    last_token = c;
                    break;

                case '}':
                    // current map
                    JSONElement self_map = bases.poll();
                    // set the last value
                    if (StringUtil.isNotEmpty(sb) || force_set_string) {
                        self_map.offer(keys.poll(), parseValue(sb.toString(), force_set_string));
                        StringUtil.clear(sb);
                        force_set_string = false;
                    } else if (CharacterUtil.isColon(last_token)) {
                        self_map.offer(keys.poll(), JSONElement.theVoid());
                    }
                    // if there was no upper element, conversion is finished, ignore extra chars
                    if (isSelfTheTopElement(bases, keys, self_map)) return self_map;
                    last_token = c;
                    break;

                case ']':
                    // current list
                    JSONElement self_list = bases.poll();
                    // set the last value
                    if (StringUtil.isNotEmpty(sb) || force_set_string) {
                        self_list.offer(parseValue(sb.toString(), force_set_string));
                        StringUtil.clear(sb);
                        force_set_string = false;
                    } else if (CharacterUtil.isComma(last_token)) {
                        self_list.offer(JSONElement.theVoid());
                    }
                    // if there was no upper element, conversion is finished, ignore extra chars
                    if (isSelfTheTopElement(bases, keys, self_list)) return self_list;
                    last_token = c;
                    break;

                default:
                    if (StringUtil.isNotEmpty(sb)) {
                        last_token = CharacterUtil.NUL;
                        sb.append(c);
                    } else if (CharacterUtil.isVisibleAndNotSpace(c)) {
                        last_token = CharacterUtil.NUL;
                        force_set_string = false;
                        sb.append(c);
                    }
                    break;

            }

        }

        return JSONElement.theVoid();

    }

    private static boolean isSelfTheTopElement(Queue<JSONElement> bases, Queue<String> keys, JSONElement self) {

        // upper element exists, set self to it
        if (!bases.isEmpty()) {
            JSONElement base = bases.peek();
            if (base.isList()) {
                base.offer(self);
            } else if (base.isMap()) {
                base.offer(keys.poll(), self);
            }
            return false;
        } else {
            // no upper elements
            return true;
        }

    }

    private static JSONElement parseValue(String s, boolean force_set_string) {

        if (force_set_string) {
            return JSONElement.newPrimitive(StringUtil.unescape(s));
        } else if (StringUtil.TRUE.equalsIgnoreCase(s)) {
            return JSONElement.newPrimitive(Boolean.TRUE);
        } else if (StringUtil.FALSE.equalsIgnoreCase(s)) {
            return JSONElement.newPrimitive(Boolean.FALSE);
        } else if (StringUtil.NULL.equalsIgnoreCase(s)) {
            return JSONElement.theVoid();
        } else if (StringUtil.NAN.equals(s)) {
            return JSONElement.theVoid();
        } else if (StringUtil.UNDEFINED.equalsIgnoreCase(s)) {
            return JSONElement.theVoid();
        } else if (TypeUtil.couldCastToNumber(s)) {
            return JSONElement.newPrimitive(TypeUtil.castToNumber(s));
        } else {
            return JSONElement.newPrimitive(StringUtil.unescape(s));
        }

    }

    /**
     * {@link JSONElement} =&gt; JSON {@link String}.
     *
     * @param element the {@link JSONElement} to be converted.
     * @return the JSON {@link String} converted.
     */
    public static String analyze(JSONElement element) {

        StringBuilder sb = new StringBuilder();

        switch (element.getType()) {

            case LIST:
                sb.append('[');
                Iterator<JSONElement.Entry> iterator = element.iterator();
                while (iterator.hasNext()) {
                    sb.append(analyze(iterator.next().getElement()));
                    if (iterator.hasNext()) {
                        sb.append(',');
                    }
                }
                sb.append(']');
                break;

            case MAP:
                sb.append('{');
                Iterator<JSONElement.Entry> iterator1 = element.iterator();
                while (iterator1.hasNext()) {
                    JSONElement.Entry e = iterator1.next();
                    sb.append('"');
                    sb.append(StringUtil.escape(StringUtil.toString(e.getKey())));
                    sb.append('"');
                    sb.append(':');
                    sb.append(analyze(e.getElement()));
                    if (iterator1.hasNext()) {
                        sb.append(',');
                    }
                }
                sb.append('}');
                break;

            case PRIMITIVE:
                Object v = element.asValue();
                if (v instanceof String) {
                    sb.append('"');
                    sb.append(StringUtil.escape((String) v));
                    sb.append('"');
                } else {
                    sb.append(StringUtil.toString(v));
                }
                break;

            case VOID:
                sb.append(StringUtil.NULL);
                break;

            default:
                break;

        }

        return sb.toString();

    }

}
