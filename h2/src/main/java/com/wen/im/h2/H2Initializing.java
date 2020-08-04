package com.wen.im.h2;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 应用启动销毁前执行相关操作
 * @author: wen <br>
 * @date: 2019/05/19 21:26 <br>
 */
@Component
public class H2Initializing implements InitializingBean,DisposableBean{

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void destroy() throws Exception {

    }

}

