package com.chakilo.jiraffe;


import com.chakilo.jiraffe.model.base.JSONElement;
import org.junit.Test;

/**
 * 2019.02.13
 *
 * @author Chakilo
 */
public class JsonTest {

    @Test
    public void test1() throws Exception {

        JSONElement element = JSON.deserialize("{'a':'b','c':{'d':'e'},'f':{'g':\"h\"},'i':[{'j':'k','l':'m'},'n']}");

        assert null != element;

        assert "{\"a\":\"b\",\"c\":{\"d\":\"e\"},\"f\":{\"g\":\"h\"},\"i\":[{\"j\":\"k\",\"l\":\"m\"},\"n\"]}".equals(element.toString());

    }

    @Test
    public void test2() throws Exception {

        JSONElement element = JSON.deserialize("[123,123.45,'123.45','2019-01-02 03:04:05',true,false,null]");

        assert null != element;

        assert "[123,123.45,\"123.45\",\"2019-01-02 03:04:05\",true,false,null]".equals(element.toString());

    }

    @Test
    public void test3() throws Exception {

        JSONElement element = JSON.deserialize("{null:null}");

        assert null != element;

        assert "{\"null\":null}".equals(element.toString());

    }

    @Test
    public void test4() throws Exception {

        JSONElement element = JSON.deserialize("[,,]");

        assert null != element;

        assert "[null,null,null]".equals(element.toString());

    }

    @Test
    public void test5() throws Exception {

        JSONElement element = JSON.deserialize("[[[[[,]]]]]");

        assert null != element;

        assert "[[[[[null,null]]]]]".equals(element.toString());

    }

    @Test
    public void test6() throws Exception {

        JSONElement element = JSON.deserialize("{a:{b:}}");

        assert null != element;

        assert "{\"a\":{\"b\":null}}".equals(element.toString());

    }

    @Test
    public void test7() throws Exception {

        JSONElement element = JSON.deserialize("{a:{b:{c:{d:{e:'f'}}}}}");

        assert null != element;

        assert "{\"a\":{\"b\":{\"c\":{\"d\":{\"e\":\"f\"}}}}}".equals(element.toString());

    }

    @Test
    public void test8() throws Exception {

        JSONElement element = JSON.deserialize("[[[],[]],[[]],[],[{},{},null]]");

        assert null != element;

        assert "[[[],[]],[[]],[],[{},{},null]]".equals(element.toString());

    }

    @Test
    public void test9() throws Exception {

        JSONElement element = JSON.deserialize("{\"a\":\"'\\u7684\\t\\n\"}");

        assert null != element;

        assert "'的\t\n".equals(element.peek("a").getString());

        assert "{\"a\":\"'\\u7684\\t\\n\"}".equals(element.toString());

    }

    @Test
    public void test10() throws Exception {

        JSONElement element = JSON.deserialize("{\"a\":\"'\\ud83d\\udc4c\\t\\n\"}");

        assert null != element;

        assert "'👌\t\n".equals(element.peek("a").getString());

        assert "{\"a\":\"'\\uD83D\\uDC4C\\t\\n\"}".equals(element.toString());

    }

}
