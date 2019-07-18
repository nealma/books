package chapter4;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 自定义消息转换器
 */
public class MyMessageConverter extends AbstractHttpMessageConverter<CustomBean> {// 继承 AbstractHttpMessageConverter，实现自定义

    public MyMessageConverter() {
        // 新建一个自定义媒体类型 application/x-nealma-1
        super(new MediaType("application", "x-nealma", Charset.forName("UTF-8")));
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return CustomBean.class.isAssignableFrom(aClass); // 表面只处理 CustomBean 这个类
    }

    /**
     * 重写 readInternal() 方法，处理请求的数据
     */
    @Override
    protected CustomBean readInternal(Class<? extends CustomBean> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        String temp = StreamUtils.copyToString(httpInputMessage.getBody(), Charset.forName("UTF-8"));
        String[] split = temp.split("-");
        return new CustomBean(split[0], split[1], split[2]);
    }

    /**
     * 重写 writeInternal() 方法，处理如何输出数据到 response 中
     */
    @Override
    protected void writeInternal(CustomBean customBean, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        httpOutputMessage.getBody().write((customBean.getId() + customBean.getName() + customBean.getGender()).getBytes());
    }
}
