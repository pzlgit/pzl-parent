package com.pzl.program.frametool.oauth2;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * OAuth2作用
 * <p>
 * 1、开发系统间授权
 * 2、实现单点登录（sso、共享session）-一处应用登录，其它系统平台也登录，一次登录，各处运行。
 * 如何实现单点登录?
 * (1)   session的广播机制 (早期)  --session的复制
 * session是一种会话机制，在一次会话范围中使用，服务端技术，默认过期时间30分钟，session基于cookie实现。
 * 关闭浏览器，session中的数据不会销毁，这是因为session基于cookie实现，将数据存储到浏览器，cookie默认是会话级别，cookie相当于打开session的一把钥匙，只是cookie数据没了，但是sessio数据还是存在，只是钥匙没了。
 * 缺点：应用间session复制效率低
 * （2）cookie加redis存储
 * 登录之后，将用户信息 存储到cookie中的key值，redis存储value值。每次访问时，拿着cookie值进行访问，到redis中获取值。
 * <p>
 * 缺点：需要向数据库中存储数据，也会影响效率。
 * （3）OAuth2的令牌机制 (JWT)      --大部分使用现在的方式
 * 不需要向数据库中存储数据，只是在客户端进行操作，用约定好的机制进行加密，生成一定规则的字符串（token）。
 * 把token值放到路径后面，登录其他应用时，拿着token进行访问。
 * <p>
 * oauth2提供一种解决方案，jwt是这种解决方案的具体实现。
 * <p>
 * JWT 令牌
 * <p>
 * 1、传统用户身份验证
 * Internet服务无法与用户身份验证分开。一般过程如下：
 * <p>
 * 用户向服务器发送用户名和密码。
 * 验证服务器后，相关数据（如用户角色，登录时间等）将保存在当前会话中。
 * 服务器向用户返回session_id，session信息都会写入到用户的Cookie。
 * 用户的每个后续请求都将通过在Cookie中取出session_id传给服务器。
 * 服务器收到session_id并对比之前保存的数据，确认用户的身份。
 * 这种模式最大的问题是，没有分布式架构，无法支持横向扩展。
 * <p>
 * 2、JWT 使用
 *
 * @author pzl
 * @version 1.0
 * @date 2020-04-24
 */
public class JwtUtil {

    //项目名
    public static final String SUBJECT = "pzl";
    //秘钥,不能太短，否则会报错
    public static final String APPSECRET = "pzljwt";
    //过期时间，毫秒，30分钟
    public static final long EXPIRE = 1000 * 60 * 30;

    /**
     * 生成JWT Token 令牌
     *
     * @param id   唯一id
     * @param name 名称
     * @return jwt token
     */
    public static String generateJwtToken(String id, String name) {
        //如果id和name为空，则不符合生成token条件
        if (StringUtils.isBlank(id) || StringUtils.isBlank(name)) {
            return null;
        }
        //生成token过程
        String token = Jwts.builder().setSubject(SUBJECT)
                .claim("id", id)
                .claim("name", name)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, APPSECRET)
                .compact();
        return token;
    }

    /**
     * 校验 token
     *
     * @param token token
     * @return Claims
     */
    public static Claims checkJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(APPSECRET).parseClaimsJws(token).getBody();
        return claims;
    }

    public static void main(String[] args) {
        //生成jwt token
        String token = generateJwtToken("1001", "pzl");
        System.out.println(token);
        //校验token
        Claims claims = checkJWT(token);
        String id = (String) claims.get("id");
        String name = (String) claims.get("name");
        System.out.println("id=" + id + ",name=" + name);
    }

}
