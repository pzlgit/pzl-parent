package com.pzl.program.frametool.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * Redis 操作工具类
 * -------------------------------------------------------------------------------------
 * RedisTemplate使用的是JdkSerializationRedisSerializer,
 * 存入数据会将数据先序列化成字节数组然后在存入Redis数据库。
 * StringRedisTemplate使用的是StringRedisSerializer,
 * 当你的redis数据库里面本来存的是字符串数据或者你要存取的数据就是字符串类型数据的时候，
 * 那么你就使用StringRedisTemplate即可。
 * <p>
 * 但是如果你的数据是复杂的对象类型，而取出的时候又不想做任何的数据转换，
 * 直接从Redis里面取出一个对象，那么使用RedisTemplate是更好的选择。
 * -------------------------------------------------------------------------------------
 * RedisTemplate中定义了5种数据结构操作
 * <p>
 * redisTemplate.opsForValue();　　//操作字符串
 * redisTemplate.opsForHash();　　 //操作hash
 * redisTemplate.opsForList();　　 //操作list
 * redisTemplate.opsForSet();　　  //操作set
 * redisTemplate.opsForZSet();　 　//操作有序set
 * --------------------------------------------------------------------------------------
 * StringRedisTemplate常用操作
 * <p>
 * stringRedisTemplate.opsForValue().set("test", "100",60*10,TimeUnit.SECONDS);//向redis里存入数据和设置缓存时间
 * stringRedisTemplate.boundValueOps("test").increment(-1);//val做-1操作
 * stringRedisTemplate.opsForValue().get("test")//根据key获取缓存中的val
 * stringRedisTemplate.boundValueOps("test").increment(1);//val +1
 * stringRedisTemplate.getExpire("test")//根据key获取过期时间
 * stringRedisTemplate.getExpire("test",TimeUnit.SECONDS)//根据key获取过期时间并换算成指定单位
 * stringRedisTemplate.delete("test");//根据key删除缓存
 * stringRedisTemplate.hasKey("546545");//检查key是否存在，返回boolean值
 * stringRedisTemplate.opsForSet().add("red_123", "1","2","3");//向指定key中存放set集合
 * stringRedisTemplate.expire("red_123",1000 , TimeUnit.MILLISECONDS);//设置过期时间
 * stringRedisTemplate.opsForSet().isMember("red_123", "1")//根据key查看集合中是否存在指定数据
 * stringRedisTemplate.opsForSet().members("red_123");//根据key获取set集合
 *
 * @author pzl
 * @date 2020-04-01
 */
@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /*------------------------key相关操作---------------------------*/

    /**
     * 删除key
     *
     * @param key key
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 批量删除key
     *
     * @param keys key列表
     */
    public void delete(Collection<String> keys) {
        stringRedisTemplate.delete(keys);
    }

    /**
     * 序列化key
     *
     * @param key key
     * @return 序列化后的key
     */
    public byte[] dump(String key) {
        return stringRedisTemplate.dump(key);
    }

    /**
     * stringRedisTemplate是否存在key
     *
     * @param key key
     * @return boolean
     */
    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * redisTemplate是否存在key
     *
     * @param key key
     * @return boolean
     */
    private boolean exists(byte[] key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     *
     * @param key     key
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return boolean
     */
    public boolean expire(String key, Long timeout, TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }

    /**
     * 设置过期时间(以Unix时间戳格式设置键的到期时间。在到期时间后，键将在Redis中失效不可用。)
     *
     * @param key  key
     * @param date 时间戳
     * @return boolean
     */
    public boolean expireAt(String key, Date date) {
        return stringRedisTemplate.expireAt(key, date);
    }

    /**
     * 查找匹配的key(查找所有符合给定模式pattern的key)
     * eg： key* 查找以"key"开头的所有key
     *
     * @param pattern 匹配模式
     * @return 匹配到的key列表
     */
    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    /**
     * 将当前数据库的 key 移动到给定的数据库db当中
     *
     * @param key     key
     * @param dbIndex 数据库索引(0~15)
     * @return boolean
     */
    public boolean move(String key, int dbIndex) {
        return stringRedisTemplate.move(key, dbIndex);
    }

    /**
     * 移除 key 的过期时间，key将持久保持
     *
     * @param key key
     * @return boolean
     */
    public boolean persist(String key) {
        return stringRedisTemplate.persist(key);
    }

    /**
     * 返回key的剩余的过期时间
     *
     * @param key  key
     * @param unit 时间单位
     *             时间单位,天:TimeUnit.DAYS 小时:TimeUnit.HOURS 分钟:TimeUnit.MINUTES
     *             秒:TimeUnit.SECONDS 毫秒:TimeUnit.MILLISECONDS
     * @return 过期时间
     */
    public Long getExpire(String key, TimeUnit unit) {
        return stringRedisTemplate.getExpire(key, unit);
    }

    /**
     * 返回 key 的剩余的过期时间
     *
     * @param key key
     * @return 过期时间
     */
    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key);
    }

    /**
     * 从当前数据库中随机返回一个 key
     *
     * @return 随机key
     */
    public String randomKey() {
        return stringRedisTemplate.randomKey();
    }

    /**
     * 修改key的名称
     *
     * @param oldKey 旧key
     * @param newKey 新key
     */
    public void rename(String oldKey, String newKey) {
        stringRedisTemplate.rename(oldKey, newKey);
    }

    /**
     * 仅当 newkey 不存在时，将 oldKey 改名为 newkey
     *
     * @param oldKey 旧key
     * @param newKey 新key
     * @return boolean
     */
    public boolean renameIfAbsent(String oldKey, String newKey) {
        return stringRedisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 返回 key 所储存的值的类型
     *
     * @param key key
     * @return 值类型
     */
    public DataType type(String key) {
        return stringRedisTemplate.type(key);
    }


    /*------------------------string相关操作-----------------------------*/

    /**
     * 设置指定key的值
     *
     * @param key   key
     * @param value 值
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取指定key的值
     *
     * @param key key
     * @return 值
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 截取key中字符串值的子字符
     *
     * @param key   key
     * @param start 开始索引
     * @param end   结束索引
     * @return 值
     */
    public String getRange(String key, long start, long end) {
        return stringRedisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 将给定key的值设为value,并返回key的旧值(old value)
     *
     * @param key   key
     * @param value 设定值
     * @return 旧值
     */
    public String getAndSet(String key, String value) {
        return stringRedisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 对key所储存的字符串值,获取指定偏移量上的位(bit)
     *
     * @param key    key
     * @param offset 偏移量
     * @return boolean
     */
    public boolean getBit(String key, long offset) {
        return stringRedisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 批量获取值
     *
     * @param keys key列表
     * @return 值列表
     */
    public List<String> multiGet(Collection<String> keys) {
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 设置ASCII码,字符串'a'的ASCII码是97, 转为二进制是'01100001', 此方法是将二进制第offset位值变为value
     *
     * @param key    key
     * @param offset 位置
     * @param value  值,true为1, false为0
     * @return
     */
    public boolean setBit(String key, long offset, boolean value) {
        return stringRedisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 将值 value 关联到 key,并将 key 的过期时间设为 timeout
     *
     * @param key     key
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位,天:TimeUnit.DAYS 小时:TimeUnit.HOURS 分钟:TimeUnit.MINUTES
     *                秒:TimeUnit.SECONDS 毫秒:TimeUnit.MILLISECONDS
     */
    public void setEx(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 只有在 key 不存在时设置 key 的值
     *
     * @param key   key
     * @param value 值
     * @return 之前已经存在返回false, 不存在返回true
     */
    public boolean setIfAbsent(String key, String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 用 value 参数覆写给定 key 所储存的字符串值,从偏移量 offset 开始
     *
     * @param key    key
     * @param value  值
     * @param offset 从指定位置开始覆写
     */
    public void setRange(String key, String value, long offset) {
        stringRedisTemplate.opsForValue().set(key, value, offset);
    }

    /**
     * 获取字符串的长度
     *
     * @param key key
     * @return 字符串的长度
     */
    public Long size(String key) {
        return stringRedisTemplate.opsForValue().size(key);
    }

    /**
     * 批量添加
     *
     * @param maps 数据(key=map.key,value=map.value)
     */
    public void multiSet(Map<String, String> maps) {
        stringRedisTemplate.opsForValue().multiSet(maps);
    }

    /**
     * 同时设置一个或多个 key-value 对,当且仅当所有给定 key 都不存在
     *
     * @param maps 数据(key=map.key,value=map.value)
     * @return 之前已经存在返回false, 不存在返回true
     */
    public boolean multiSetIfAbsent(Map<String, String> maps) {
        return stringRedisTemplate.opsForValue().multiSetIfAbsent(maps);
    }

    /**
     * 增加(自增长),负数则为自减
     *
     * @param key       key
     * @param increment 增长的值
     * @return 值
     */
    public Long incrBy(String key, long increment) {
        return stringRedisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 增加(自增长),负数则为自减
     *
     * @param key       key
     * @param increment 增长的值
     * @return 值
     */
    public Double incrByFloat(String key, double increment) {
        return stringRedisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 追加到末尾
     *
     * @param key   key
     * @param value 值
     * @return int
     */
    public Integer append(String key, String value) {
        return stringRedisTemplate.opsForValue().append(key, value);
    }


    /*-----------------------------------hash相关操作-----------------------------*/

    /**
     * 获取存储在哈希表中指定字段的值
     *
     * @param key   key
     * @param field 字段
     * @return 值
     */
    public Object hGet(String key, String field) {
        return stringRedisTemplate.opsForHash().get(key, field);
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key key
     * @return 所有字段的值
     */
    public Map<Object, Object> hGetAll(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取所有给定字段的值(可以指定字段)
     *
     * @param key    key
     * @param fields 字段
     * @return 指定字段的值
     */
    public List<Object> hMultiGet(String key, Collection<Object> fields) {
        return stringRedisTemplate.opsForHash().multiGet(key, fields);
    }

    /**
     * hash结构中添加值
     *
     * @param key     key
     * @param hashKey name
     * @param value   value
     */
    public void hPut(String key, String hashKey, String value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 批量设置hash结构数据
     *
     * @param key  key
     * @param maps (filed=map.key,value=map.value)
     */
    public void hPutAll(String key, Map<String, String> maps) {
        stringRedisTemplate.opsForHash().putAll(key, maps);
    }

    /**
     * 仅当hashKey不存在时才设置
     *
     * @param key     key
     * @param hashKey filed
     * @param value   值
     * @return boolean
     */
    public boolean hPutIfAbsent(String key, String hashKey, String value) {
        return stringRedisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 删除一个或多个哈希表字段
     *
     * @param key    key
     * @param fields 字段
     * @return long
     */
    public Long hDelete(String key, Object... fields) {
        return stringRedisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 查看哈希表key中,指定的字段是否存在
     *
     * @param key   key
     * @param field 字段
     * @return boolean
     */
    public boolean hExists(String key, String field) {
        return stringRedisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key       key
     * @param field     字段
     * @param increment 增量值
     * @return 自增长后的值
     */
    public Long hIncrBy(String key, Object field, long increment) {
        return stringRedisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key   key
     * @param field 字段
     * @param delta 增量值
     * @return 自增长后的值
     */
    public Double hIncrByFloat(String key, Object field, double delta) {
        return stringRedisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * 获取所有哈希表中的字段
     *
     * @param key key
     * @return hash中的所有字段
     */
    public Set<Object> hKeys(String key) {
        return stringRedisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取哈希表中字段的数量
     *
     * @param key key
     * @return hash中字段的数量
     */
    public Long hSize(String key) {
        return stringRedisTemplate.opsForHash().size(key);
    }

    /**
     * 获取哈希表中所有值
     *
     * @param key key
     * @return hash表所有值
     */
    public List<Object> hValues(String key) {
        return stringRedisTemplate.opsForHash().values(key);
    }

    /**
     * 迭代哈希表中的键值对
     *
     * @param key     key
     * @param options 迭代当前数据库中的数据库键
     * @return data
     */
    public Cursor<Entry<Object, Object>> hScan(String key, ScanOptions options) {
        return stringRedisTemplate.opsForHash().scan(key, options);
    }


    /*------------------------------list相关操作---------------------------------------*/

    /**
     * 通过索引获取列表中的元素
     *
     * @param key   key
     * @param index 索引
     * @return value
     */
    public String lIndex(String key, long index) {
        return stringRedisTemplate.opsForList().index(key, index);
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @param key   key
     * @param start 开始位置, 0是开始位置
     * @param end   结束位置, -1返回所有
     * @return 值
     */
    public List<String> lRange(String key, long start, long end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 存储在list头部
     *
     * @param key   key
     * @param value 值
     * @return long
     */
    public Long lLeftPush(String key, String value) {
        return stringRedisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 批量添加数据到list
     *
     * @param key   key
     * @param value 值
     * @return long
     */
    public Long lLeftPushAll(String key, String... value) {
        return stringRedisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * 批量添加数据到list
     *
     * @param key   key
     * @param value value
     * @return long
     */
    public Long lLeftPushAll(String key, Collection<String> value) {
        return stringRedisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * 当list存在的时候才加入值
     *
     * @param key   key
     * @param value 值
     * @return long
     */
    public Long lLeftPushIfPresent(String key, String value) {
        return stringRedisTemplate.opsForList().leftPushIfPresent(key, value);
    }

    /**
     * 如果pivot存在,再pivot前面添加值
     *
     * @param key   key
     * @param pivot 值
     * @param value 要添加的值
     * @return long
     */
    public Long lLeftPush(String key, String pivot, String value) {
        return stringRedisTemplate.opsForList().leftPush(key, pivot, value);
    }

    /**
     * rightpush 添加数据
     *
     * @param key   key
     * @param value 值
     * @return long
     */
    public Long lRightPush(String key, String value) {
        return stringRedisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * rightpush 批量添加数据
     *
     * @param key   key
     * @param value 值
     * @return long
     */
    public Long lRightPushAll(String key, String... value) {
        return stringRedisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * rightpush 批量添加数据
     *
     * @param key   key
     * @param value 值
     * @return long
     */
    public Long lRightPushAll(String key, Collection<String> value) {
        return stringRedisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * 为已存在的列表添加值
     *
     * @param key   key
     * @param value 值
     * @return long
     */
    public Long lRightPushIfPresent(String key, String value) {
        return stringRedisTemplate.opsForList().rightPushIfPresent(key, value);
    }

    /**
     * 在pivot元素的右边添加值
     *
     * @param key   key
     * @param pivot 值
     * @param value 添加的新值
     * @return long
     */
    public Long lRightPush(String key, String pivot, String value) {
        return stringRedisTemplate.opsForList().rightPush(key, pivot, value);
    }

    /**
     * 通过索引设置列表元素的值
     *
     * @param key   key
     * @param index 位置
     * @param value 值
     */
    public void lSet(String key, long index, String value) {
        stringRedisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 移出并获取列表的第一个元素
     *
     * @param key key
     * @return 删除的元素
     */
    public String lLeftPop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    /**
     * 移出并获取列表的第一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     *
     * @param key     key
     * @param timeout 等待时间
     * @param unit    时间单位
     * @return 元素
     */
    public String lBLeftPop(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.opsForList().leftPop(key, timeout, unit);
    }

    /**
     * 移除并获取列表最后一个元素
     *
     * @param key key
     * @return 删除的元素
     */
    public String lRightPop(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    /**
     * 移出并获取列表的最后一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param key     key
     * @param timeout 等待时间
     * @param unit    时间单位
     * @return 元素
     */
    public String lBRightPop(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.opsForList().rightPop(key, timeout, unit);
    }

    /**
     * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
     *
     * @param sourceKey      原始key
     * @param destinationKey 目标key
     * @return 元素
     */
    public String lRightPopAndLeftPush(String sourceKey, String destinationKey) {
        return stringRedisTemplate.opsForList().rightPopAndLeftPush(sourceKey,
                destinationKey);
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它；如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @param sourceKey      原始key
     * @param destinationKey 目标key
     * @param timeout        过期时间
     * @param unit           时间单位
     * @return 元素
     */
    public String lBRightPopAndLeftPush(String sourceKey, String destinationKey,
                                        long timeout, TimeUnit unit) {
        return stringRedisTemplate.opsForList().rightPopAndLeftPush(sourceKey,
                destinationKey, timeout, unit);
    }

    /**
     * 删除集合中值等于value的元素
     *
     * @param key   key
     * @param index index=0, 删除所有值等于value的元素; index>0, 从头部开始删除第一个值等于value的元素;
     *              index<0, 从尾部开始删除第一个值等于value的元素;
     * @param value 值
     * @return long
     */
    public Long lRemove(String key, long index, String value) {
        return stringRedisTemplate.opsForList().remove(key, index, value);
    }

    /**
     * 裁剪list
     *
     * @param key   ket
     * @param start 开始索引
     * @param end   结束索引
     */
    public void lTrim(String key, long start, long end) {
        stringRedisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * 获取列表长度
     *
     * @param key key
     * @return 列表长度
     */
    public Long lLen(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }


    /*---------------------------set相关操作-------------------------------*/

    /**
     * set添加元素
     *
     * @param key    key
     * @param values 值
     * @return long
     */
    public Long sAdd(String key, String... values) {
        return stringRedisTemplate.opsForSet().add(key, values);
    }

    /**
     * set移除元素
     *
     * @param key    key
     * @param values 值
     * @return long
     */
    public Long sRemove(String key, Object... values) {
        return stringRedisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 移除并返回集合的一个随机元素
     *
     * @param key key
     * @return 随机元素
     */
    public String sPop(String key) {
        return stringRedisTemplate.opsForSet().pop(key);
    }

    /**
     * 将元素value从一个集合移到另一个集合
     *
     * @param key     key
     * @param value   值
     * @param destKey 目标key
     * @return boolean
     */
    public boolean sMove(String key, String value, String destKey) {
        return stringRedisTemplate.opsForSet().move(key, value, destKey);
    }

    /**
     * 获取集合的大小
     *
     * @param key key
     * @return 集合大小
     */
    public Long sSize(String key) {
        return stringRedisTemplate.opsForSet().size(key);
    }

    /**
     * 判断集合是否包含value
     *
     * @param key   key
     * @param value 值
     * @return boolean
     */
    public boolean sIsMember(String key, Object value) {
        return stringRedisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取两个集合的交集
     *
     * @param key      key1
     * @param otherKey key2
     * @return 集合交集
     */
    public Set<String> sIntersect(String key, String otherKey) {
        return stringRedisTemplate.opsForSet().intersect(key, otherKey);
    }

    /**
     * 获取key集合与多个集合的交集
     *
     * @param key       key1
     * @param otherKeys key列表
     * @return 集合交集
     */
    public Set<String> sIntersect(String key, Collection<String> otherKeys) {
        return stringRedisTemplate.opsForSet().intersect(key, otherKeys);
    }

    /**
     * key集合与otherKey集合的交集存储到destKey集合中
     *
     * @param key      key
     * @param otherKey key1
     * @param destKey  目标key
     * @return long
     */
    public Long sIntersectAndStore(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForSet().intersectAndStore(key, otherKey,
                destKey);
    }

    /**
     * key集合与多个集合的交集存储到destKey集合中
     *
     * @param key       key
     * @param otherKeys key1
     * @param destKey   目标key
     * @return long
     */
    public Long sIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return stringRedisTemplate.opsForSet().intersectAndStore(key, otherKeys, destKey);
    }

    /**
     * 获取两个集合的并集
     *
     * @param key       key1
     * @param otherKeys key2
     * @return 集合并集
     */
    public Set<String> sUnion(String key, String otherKeys) {
        return stringRedisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * 获取key集合与多个集合的并集
     *
     * @param key       key1
     * @param otherKeys key列表
     * @return 集合并集
     */
    public Set<String> sUnion(String key, Collection<String> otherKeys) {
        return stringRedisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * key集合与otherKey集合的并集存储到destKey中
     *
     * @param key      key1
     * @param otherKey key2
     * @param destKey  目标key
     * @return long
     */
    public Long sUnionAndStore(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * key集合与多个集合的并集存储到destKey中
     *
     * @param key       key
     * @param otherKeys key列表
     * @param destKey   目标key
     * @return long
     */
    public Long sUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return stringRedisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 获取两个集合的差集
     *
     * @param key      key1
     * @param otherKey key2
     * @return 集合差集
     */
    public Set<String> sDifference(String key, String otherKey) {
        return stringRedisTemplate.opsForSet().difference(key, otherKey);
    }

    /**
     * 获取key集合与多个集合的差集
     *
     * @param key       key
     * @param otherKeys key列表
     * @return 集合差集
     */
    public Set<String> sDifference(String key, Collection<String> otherKeys) {
        return stringRedisTemplate.opsForSet().difference(key, otherKeys);
    }

    /**
     * key集合与otherKey集合的差集存储到destKey中
     *
     * @param key      key1
     * @param otherKey key2
     * @param destKey  目标key
     * @return long
     */
    public Long sDifference(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForSet().differenceAndStore(key, otherKey, destKey);
    }

    /**
     * key集合与多个集合的差集存储到destKey中
     *
     * @param key       key
     * @param otherKeys key列表
     * @param destKey   目标key
     * @return long
     */
    public Long sDifference(String key, Collection<String> otherKeys, String destKey) {
        return stringRedisTemplate.opsForSet().differenceAndStore(key, otherKeys, destKey);
    }

    /**
     * 获取集合所有元素
     *
     * @param key key
     * @return 集合所有元素
     */
    public Set<String> setMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /**
     * 随机获取集合中的一个元素
     *
     * @param key key
     * @return 随机元素
     */
    public String sRandomMember(String key) {
        return stringRedisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 随机获取集合中count个元素
     *
     * @param key   key
     * @param count 数量
     * @return 随机元素
     */
    public List<String> sRandomMembers(String key, long count) {
        return stringRedisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 随机获取集合中count个元素并且去除重复的
     *
     * @param key   key
     * @param count 数量
     * @return 去重数据
     */
    public Set<String> sDistinctRandomMembers(String key, long count) {
        return stringRedisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    /**
     * 非阻塞的方式实现key值的查找,绝大多数情况下是可以替代keys命令的,可选性更强
     *
     * @param key     key
     * @param options 匹配
     * @return data
     */
    public Cursor<String> sScan(String key, ScanOptions options) {
        return stringRedisTemplate.opsForSet().scan(key, options);
    }


    /*----------------------------zSet相关操作-----------------------------------*/

    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     *
     * @param key   key
     * @param value 值
     * @param score 分数
     * @return boolean
     */
    public boolean zAdd(String key, String value, double score) {
        return stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 批量添加元素
     *
     * @param key    key
     * @param values 值集合
     * @return long
     */
    public Long zAdd(String key, Set<TypedTuple<String>> values) {
        return stringRedisTemplate.opsForZSet().add(key, values);
    }

    /**
     * 移除元素
     *
     * @param key    key
     * @param values 值
     * @return long
     */
    public Long zRemove(String key, Object... values) {
        return stringRedisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 增加元素的score值,并返回增加后的值
     *
     * @param key   key
     * @param value 值
     * @param delta 增加评分值
     * @return 增加分数后的评分score
     */
    public Double zIncrementScore(String key, String value, double delta) {
        return stringRedisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
     *
     * @param key   key
     * @param value 值
     * @return 0表示第一位
     */
    public Long zRank(String key, Object value) {
        return stringRedisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 返回元素在集合的排名,按元素的score值由大到小排列
     *
     * @param key   key
     * @param value 值
     * @return long
     */
    public Long zReverseRank(String key, Object value) {
        return stringRedisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 获取集合的元素,从小到大排序
     *
     * @param key   key
     * @param start 开始位置
     * @param end   结束位置,-1查询所有
     * @return 集合元素
     */
    public Set<String> zRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取集合元素,并且把score值也获取
     *
     * @param key   key
     * @param start 开始索引
     * @param end   结束索引
     * @return 集合元素
     */
    public Set<TypedTuple<String>> zRangeWithScores(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 根据Score值查询集合元素
     *
     * @param key key
     * @param min 最小值
     * @param max 最大值
     * @return 集合元素
     */
    public Set<String> zRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 根据Score值查询集合元素,从小到大排序
     *
     * @param key key
     * @param min 最小值
     * @param max 最大值
     * @return 集合元素
     */
    public Set<TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }

    /**
     * 根据Score值查询集合元素,从小到大排序，并截断集合
     *
     * @param key   key
     * @param min   最小值
     * @param max   最大值
     * @param start 开始索引
     * @param end   结束索引
     * @return 集合元素
     */
    public Set<TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max, long start, long end) {
        return stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, start, end);
    }

    /**
     * 获取集合的元素, 从大到小排序
     *
     * @param key   key
     * @param start 开始索引
     * @param end   结束索引
     * @return 集合元素
     */
    public Set<String> zReverseRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取集合的元素, 从大到小排序, 并返回score值
     *
     * @param key   key
     * @param start 开始索引
     * @param end   结束索引
     * @return 集合元素
     */
    public Set<TypedTuple<String>> zReverseRangeWithScores(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key key
     * @param min 最小值
     * @param max 最大值
     * @return 集合元素
     */
    public Set<String> zReverseRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key key
     * @param min 最小值
     * @param max 最大值
     * @return 集合元素
     */
    public Set<TypedTuple<String>> zReverseRangeByScoreWithScores(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序，并截断集合
     *
     * @param key   key
     * @param min   最小值
     * @param max   最大值
     * @param start 开始索引
     * @param end   结束索引
     * @return 集合元素
     */
    public Set<String> zReverseRangeByScore(String key, double min, double max, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max, start, end);
    }

    /**
     * 根据score值获取集合元素数量
     *
     * @param key key
     * @param min 最小值
     * @param max 最大值
     * @return long
     */
    public Long zCount(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 获取集合大小
     *
     * @param key key
     * @return long
     */
    public Long zSize(String key) {
        return stringRedisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取集合大小
     *
     * @param key key
     * @return long
     */
    public Long zZCard(String key) {
        return stringRedisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 获取集合中value元素的score值
     *
     * @param key   key
     * @param value 值
     * @return score值
     */
    public Double zScore(String key, Object value) {
        return stringRedisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 移除指定索引位置的成员
     *
     * @param key   key
     * @param start 开始索引
     * @param end   结束索引
     * @return long
     */
    public Long zRemoveRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 根据指定的score值的范围来移除成员
     *
     * @param key key
     * @param min 最小值
     * @param max 最大值
     * @return long
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * 获取key和otherKey的并集并存储在destKey中
     *
     * @param key      key1
     * @param otherKey key2
     * @param destKey  目标key
     * @return long
     */
    public Long zUnionAndStore(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * 获取key和otherKeys的并集并存储在destKey中
     *
     * @param key       key
     * @param otherKeys key列表
     * @param destKey   目标key
     * @return long
     */
    public Long zUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        return stringRedisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 集合交集
     *
     * @param key      key1
     * @param otherKey key2
     * @param destKey  目标key
     * @return long
     */
    public Long zIntersectAndStore(String key, String otherKey, String destKey) {
        return stringRedisTemplate.opsForZSet().intersectAndStore(key, otherKey,
                destKey);
    }

    /**
     * 集合交集
     *
     * @param key       key
     * @param otherKeys key列表
     * @param destKey   目标key
     * @return long
     */
    public Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        return stringRedisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey);
    }

    /**
     * 非阻塞的方式实现key值的查找,绝大多数情况下是可以替代keys命令的,可选性更强
     *
     * @param key     key
     * @param options 匹配
     * @return data
     */
    public Cursor<TypedTuple<String>> zScan(String key, ScanOptions options) {
        return stringRedisTemplate.opsForZSet().scan(key, options);
    }

}