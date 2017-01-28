package Sample;

import model.*;
import pipeline.MultiJsonFilePipeline;
import processor.DetailPageProcessor;
import processor.ListPageProcessor;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import javax.management.JMException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mian on 2017/1/12.
 */
public class ListSample {
    public static void main(String[] args) throws JMException {
        List<ExtractField> extractFields = new ArrayList<ExtractField>();

        ExtractField field = new ExtractField();
        field.setFieldName("Author");
        field.setFieldSourceType(FieldSourceType.Html);
        field.setExpressionType(ExpressionType.XPath);
        field.setExpressionValue("//a[@class='article-author-name']/text()");
        field.setNeed(true);

        extractFields.add(field);

        field = new ExtractField();
        field.setFieldName("Content");
        field.setFieldSourceType(FieldSourceType.Html);
        field.setExpressionType(ExpressionType.XPath);
        field.setExpressionValue("//div[@class='article article-text']/@data-text");

        extractFields.add(field);

        field = new ExtractField();
        field.setFieldName("Time");
        field.setFieldSourceType(FieldSourceType.Html);
        field.setExpressionType(ExpressionType.XPath);
        field.setExpressionValue("//span[@class='article-date']/text()");

        extractFields.add(field);



        ListPageProcessor processor = new ListPageProcessor();
        processor.setRetryTimes(3);
        processor.setSleepTime(100);
        processor.setTimeOut(10000);

        List<Expression> expressions = new ArrayList<>();
        List<LinksExtractRule> rules = new ArrayList<>();

        Expression expression = new Expression();
        expression.setExpressionType(ExpressionType.Css);
        expression.setArguments(new Object[] {"div.pager-content"});
        expressions.add(expression);

        expression = new Expression();
        expression.setExpressionType(ExpressionType.Links);
        expressions.add(expression);

        LinksExtractRule rule = new LinksExtractRule();
        rule.setName("paged");
        rule.setExpressions(expressions);
        rules.add(rule);

        processor.setTargetRequestRules(rules);

        processor.setExtractFields(extractFields);

        LocalDateTime start = LocalDateTime.now();
        Spider spider = Spider.create(processor)
                //从"http://baozoumanhua.com/text"开始抓
                .addUrl("http://baozoumanhua.com/text")
                .addPipeline(new ConsolePipeline())
                //保存到JSON文件
                .addPipeline(new MultiJsonFilePipeline("D:\\webmagic\\"))
                .thread(5);

        SpiderMonitor.instance().register(spider);
        spider.run();

        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        System.out.println(duration.getSeconds());
    }
}
