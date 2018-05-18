package org.channel.tester.vo;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Set;

/**
 * @author channel<zclsoft@163.com>
 * @since 2018/05/03 09:51.
 */
@Data
@Builder
public class WebTestVO {
    private String beanName;
    private boolean jsonReturn;
    private boolean jsonParameter;
    private String url;
    private Set<RequestMethod> requestMethod;
    private List<List<WebTestParam>> parameter;
}
