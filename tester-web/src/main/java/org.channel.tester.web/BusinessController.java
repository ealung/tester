package org.channel.tester.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author zhangchanglu
 * @since 2018/05/18 17:57.
 */
@Controller
@RequestMapping(value = "business")
public class BusinessController {
    @RequestMapping(value = "hello", method = RequestMethod.GET)
    @ResponseBody
    public String hello(String yourName) {
        return yourName;
    }
    @RequestMapping(value = "hello/{yourName}", method = RequestMethod.GET)
    @ResponseBody
    public String hello2(@PathVariable String yourName) {
        return yourName;
    }
    @RequestMapping(value = "hello", method = RequestMethod.POST)
    @ResponseBody
    public String helloPost(String yourName) {
        return yourName;
    }
    @RequestMapping(value = "hello.htm")
    public void helloHtml(HttpServletRequest request, HttpServletResponse response) throws Exception{
        PrintWriter writer = response.getWriter();
        writer.write("hello,world");
        writer.flush();
    }
}
