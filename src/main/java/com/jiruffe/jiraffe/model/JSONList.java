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

package com.jiruffe.jiraffe.model;

import com.jiruffe.jiraffe.util.Defaults;
import com.jiruffe.jiraffe.util.TypeUtil;

import java.util.*;
import java.util.function.Consumer;

/**
 * JSON list []
 *
 * @author Jiruffe
 * 2018.10.23
 */
final class JSONList extends JSONElement {

    private final List<JSONElement> _sub_elements;

    JSONList() {
        this(Defaults.list());
    }

    JSONList(List<JSONElement> sub_elements) {
        _sub_elements = sub_elements;
    }

    @Override
    public boolean isEmpty() {
        return _sub_elements.isEmpty();
    }

    @Override
    public int size() {
        return _sub_elements.size();
    }

    @Override
    public Collection<Entry> entries() {
        Collection<Entry> entries = Defaults.collection();
        for (int i = 0; i < _sub_elements.size(); i++) {
            entries.add(new Entry(i, _sub_elements.get(i)));
        }
        return entries;
    }

    @Override
    public Collection<Object> keys() {
        Set<Object> s = new HashSet<>();
        for (int i = 0; i < _sub_elements.size(); i++) {
            s.add(i);
        }
        return s;
    }

    @Override
    public Collection<JSONElement> values() {
        return _sub_elements;
    }

    @Override
    public JSONElement peek(Object k) {
        if (TypeUtil.couldCastToInteger(k)) {
            int ik = TypeUtil.castToInteger(k);
            int sz = _sub_elements.size();
            if (ik >= 0 && ik < sz) {
                JSONElement v = _sub_elements.get(ik);
                return null != v ? v : JSONElement.theVoid();
            } else {
                throw new IndexOutOfBoundsException("Index: " + ik + ", Size: " + sz);
            }
        } else {
            throw new IllegalArgumentException("Argument k must be Integer");
        }
    }

    @Override
    public JSONElement poll(Object k) {
        if (TypeUtil.couldCastToInteger(k)) {
            int ik = TypeUtil.castToInteger(k);
            int sz = _sub_elements.size();
            if (ik >= 0 && ik < sz) {
                JSONElement v = _sub_elements.remove(ik);
                return null != v ? v : JSONElement.theVoid();
            } else {
                throw new IndexOutOfBoundsException("Index: " + ik + ", Size: " + sz);
            }
        } else {
            throw new IllegalArgumentException("Argument k must be Integer");
        }
    }

    @Override
    public JSONElement offer(Object v) {
        if (null == v) {
            _sub_elements.add(JSONElement.theVoid());
        } else if (v instanceof JSONElement) {
            _sub_elements.add((JSONElement) v);
        } else if (v instanceof Entry) {
            _sub_elements.add(JSONElement.newMap().offer(v));
        } else {
            _sub_elements.add(JSONElement.newPrimitive(v));
        }
        return this;
    }

    @Override
    public JSONElement offer(Object k, Object v) {
        if (TypeUtil.couldCastToInteger(k)) {
            int ik = TypeUtil.castToInteger(k);
            int sz = _sub_elements.size();
            if (ik >= 0 && ik < sz) {
                if (null == v) {
                    _sub_elements.set(ik, JSONElement.theVoid());
                } else if (v instanceof JSONElement) {
                    _sub_elements.set(ik, (JSONElement) v);
                } else if (v instanceof Entry) {
                    _sub_elements.set(ik, JSONElement.newMap().offer(v));
                } else {
                    _sub_elements.set(ik, JSONElement.newPrimitive(v));
                }
            } else {
                throw new IndexOutOfBoundsException("Index: " + ik + ", Size: " + sz);
            }
        } else {
            throw new IllegalArgumentException("Argument k must be Integer");
        }
        return this;
    }

    @Override
    public JSONElement merge(JSONElement e) {
        if (e instanceof JSONList) {
            _sub_elements.addAll(e.asList());
        } else {
            super.merge(e);
        }
        return this;
    }

    @Override
    public boolean containsKey(Object k) {
        if (null == k) {
            return false;
        } else if (TypeUtil.couldCastToInteger(k)) {
            int ik = TypeUtil.castToInteger(k);
            return ik >= 0 && ik < _sub_elements.size();
        } else {
            return false;
        }
    }

    @Override
    public boolean containsValue(Object v) {
        if (this == v) {
            return true;
        } else if (null == v) {
            return false;
        } else {
            JSONElement ev = JSONElement.newInstance(v);
            return _sub_elements.contains(JSONElement.newInstance(ev)) || equals(ev);
        }
    }

    @Override
    public List<JSONElement> asList() {
        return _sub_elements;
    }

    @Override
    public Iterator<Entry> iterator() {
        return entries().iterator();
    }

    @Override
    public void forEach(Consumer<? super Entry> action) {
        entries().forEach(action);
    }

    @Override
    public Spliterator<Entry> spliterator() {
        return entries().spliterator();
    }

}
