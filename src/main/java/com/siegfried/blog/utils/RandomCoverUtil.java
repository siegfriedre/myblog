package com.siegfried.blog.utils;

import com.siegfried.blog.enums.FilePathEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static com.siegfried.blog.constant.RedisPrefixConst.ARTICLE_COVER_COUNT;

/**
 * 获取随机封面图片url
 * @author zy_zhu
 * @date 2021/7/10 19:59
 */
@Component
public class RandomCoverUtil {

    @Autowired
    RedisTemplate redisTemplate;

    private static String base_url;
    private String cover_url;
    private String extension;
    private Integer article_cover_count;
/*    private RedisTemplate redisTemplate;

    public RandomCoverUtil(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }*/

    @Value("${aliyun.url}")
    public void setUrl(String url){
        this.base_url = url;
    }

    /**
     * baseurl为阿里云oss绑定的二级域名，coverurl是bucketname和存放路径以及图片完整文件名
     * @return 随机封面图片链接
     */
    public String get(){
        article_cover_count = (Integer) redisTemplate.boundValueOps(ARTICLE_COVER_COUNT).get();
        //目前仅支持jpg
        extension = ".jpg";
        cover_url = FilePathEnum.ARTICLE.getPath()+article_cover_count.toString()+extension;
        ++article_cover_count;
        redisTemplate.boundValueOps(ARTICLE_COVER_COUNT).set(article_cover_count%9);//0-8循环
        return base_url+"/"+cover_url;
    }
}
