/*
 *    Copyright 2018 Chakilo
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
package com.chakilo.jiraffe;

import com.chakilo.jiraffe.analyzer.DirectAnalyzer;
import com.chakilo.jiraffe.analyzer.ObjectAnalyzer;
import com.chakilo.jiraffe.analyzer.StringAnalyzer;
import com.chakilo.jiraffe.model.base.JSONElement;

/******************************************************************************
 *
 * jiraffe
 * A Java library for JSON conversion.
 *
 * https://github.com/chakilo/jiraffe
 *
 * 2018.10.23
 * @author Chakilo
 * @since 1.8
 *
 ******************************************************************************/
public abstract class JSON {

    /**
     * Serializes java object to JSONElement.
     *
     * @param o the object to be serialized.
     * @return the JSONElement serialized.
     * @throws Exception if error occurred while analyzing object.
     */
    public static JSONElement serialize(Object o) throws Exception {
        if (o instanceof JSONElement) {
            return (JSONElement) o;
        } else {
            return ObjectAnalyzer.analyze(o);
        }
    }

    /**
     * Deserializes json string to JSONElement.
     *
     * @param json the json string to be deserialized.
     * @return the JSONElement deserialized.
     * @throws Exception if error occurred while analyzing json string.
     */
    public static JSONElement deserialize(String json) throws Exception {
        return StringAnalyzer.analyze(json);
    }

    /**
     * Directly serializes java object to json string.
     *
     * @param o the object to be serialized.
     * @return the json string serialized.
     * @throws Exception if error occurred while analyzing object.
     */
    public static String serializeDirectly(Object o) throws Exception {
        if (o instanceof JSONElement) {
            return StringAnalyzer.analyze((JSONElement) o);
        } else {
            return DirectAnalyzer.analyze(o);
        }
    }

    /**
     * Directly deserializes json string to java object.
     *
     * @param json the json string to be deserialized.
     * @return the java object deserialized.
     * @throws Exception if error occurred while analyzing json string.
     */
    public static <T> T deserializeDirectly(String json, Class<T> target) throws Exception {
        if (JSONElement.class.isAssignableFrom(target)) {
            return (T) StringAnalyzer.analyze(json);
        } else {
            return DirectAnalyzer.analyze(json, target);
        }
    }

}
