package chapter3.aware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

/**
 * aware
 *
 * @author neal.ma
 * @date 2019/7/7
 * @blog nealma.com
 */
@Slf4j
@Service
public class AwareService implements BeanNameAware, ResourceLoaderAware {// 获得 Bean 名称和资源加载的服务

    private String beanName;
    private ResourceLoader resourceLoader;
    /**
     * 继承 BeanNameAware 接口，需重写 setBeanName() 方法
     */
    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * 继承 ResourceLoaderAware 接口，需重写 setResourceLoader() 方法
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void print() throws IOException {
        log.info("beanName: {}", beanName);
        Resource resource = resourceLoader.getResource("classpath:test.txt");
        log.info("resource: {}", new String(FileCopyUtils.copyToByteArray(resource.getInputStream())));
    }
}
