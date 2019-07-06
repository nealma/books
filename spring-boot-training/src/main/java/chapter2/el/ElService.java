package chapter2.el;

import lombok.Data;
import org.springframework.stereotype.Service;

/**
 * Spring EL
 *
 * @author neal.ma
 * @date 2019/7/6
 * @blog nealma.com
 */
@Service
@Data
public class ElService {
    private String name = "tina";
}
