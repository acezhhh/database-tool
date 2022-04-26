package com.acezhhh.tool.utils;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.Map;

/**
 * @author acezhhh
 * @date 2022/4/21
 */
public class CreateWordUtil {

    public static void createWord(String filePath, String templateName, Map dataMap) {
        try {
            //Configuration 用于读取ftl文件
            Configuration configuration = new Configuration();
            configuration.setDefaultEncoding("utf-8");
            configuration.setClassForTemplateLoading(CreateWordUtil.class,"/templates");
            configuration.setTemplateLoader(new ClassTemplateLoader(CreateWordUtil.class,"/templates"));
            //输出文档路径及名称
            File outFile = new File(filePath);
            //以utf-8的编码读取ftl文件
            Template template = configuration.getTemplate(templateName, "utf-8");
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"), 10240);
            template.process(dataMap, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
