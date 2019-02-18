package com.chakilo.jiraffe.model;

import com.chakilo.jiraffe.model.base.JSONElement;
import com.chakilo.jiraffe.utils.TypeUtil;
import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.*;
import java.util.function.Consumer;

/**
 * 2018.10.23
 *
 * 数组型 []
 *
 * @author Chakilo
 */
public final class JSONArray extends JSONElement {

    protected List<JSONElement> _sub_elements;

    public JSONArray() {
        this(new ArrayList<>());
    }

    public JSONArray(List<JSONElement> sub_elements) {
        _sub_elements = sub_elements;
    }

    @Override
    public Iterator<JSONElement> iterator() {
        return null != _sub_elements ? _sub_elements.iterator() : null;
    }

    @Override
    public void forEach(Consumer<? super JSONElement> action) {
        if (null != _sub_elements) {
            _sub_elements.forEach(action);
        }
    }

    @Override
    public Spliterator<JSONElement> spliterator() {
        return null != _sub_elements ? _sub_elements.spliterator() : null;
    }

    @Override
    public JSONElement peek(Object k) throws InvalidArgumentException {
        if (TypeUtil.couldCastToInteger(k)) {
            return _sub_elements.get(TypeUtil.castToInteger(k));
        } else {
            throw new InvalidArgumentException(new String[]{"k"});
        }
    }

    @Override
    public JSONElement offer(Object v) {
        if (v instanceof JSONElement) {
            _sub_elements.add((JSONElement) v);
        } else {
            _sub_elements.add(new JSONValue(v));
        }
        return this;
    }

}