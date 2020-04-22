package com.pzl.program.frametool.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Zookeeper 操作工具类
 * <p>
 * zookkper 常用操作
 * create /path data         //创建持久节点
 * create -s /path /data     //创建顺序持节节点
 * create -e /path /data     //创建临时节点
 * stat /path                //查看节点状态
 * get /path                 //获取节点
 * set /path /data           //修改节点
 * ls  /path                 //列出子项
 * ls2 /path                 //查看子节点信息和状态信息
 * delete /firstNode        //移除节点
 *
 * @author pzl
 * @date 2020-04-01
 */
@Slf4j
public class ZooKeeperUtil implements Watcher {

    private ZooKeeper zookeeper;

    //设置超时时间(单位:毫秒)
    private static final int SESSION_TIME_OUT = 20000;

    /**
     * JAVA并发工具类,使一个或多个线程等待其他线程完成各自的工作后再执行。
     */
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            log.info("Watch received event");
            countDownLatch.countDown();
        }
    }

    /**
     * 连接zookeeper
     * [关于connectString服务器地址配置]
     * 格式: 192.168.1.1:2181,192.168.1.2:2181,192.168.1.3:2181
     * 这个地址配置有多个ip:port之间逗号分隔,底层操作
     *
     * @param host 地址
     * @throws Exception ex    
     */
    public void connectZookeeper(String host) throws Exception {
        //跳过sasl验证
        System.setProperty("zookeeper.sasl.client", "false");
        zookeeper = new ZooKeeper(host, SESSION_TIME_OUT, this);
        countDownLatch.await();
        log.info("zookeeper connection success");
    }

    /**
     * 创建持久节点
     * 1. CreateMode.PERSISTENT ：持久节点，一旦创建就保存到硬盘上面
     * 2.  CreateMode.SEQUENTIAL ： 顺序持久节点
     * 3.  CreateMode.EPHEMERAL ：临时节点，创建以后如果断开连接则该节点自动删除
     * 4.  CreateMode.EPHEMERAL_SEQUENTIAL ：顺序临时节点
     *
     * @param path 路径
     * @param data 数据
     * @return data
     * @throws Exception ex
     */
    public String createPersistentNode(String path, String data) throws Exception {
        if (!isExitZKPath(path)) {
            log.error("当前节点已经存在");
            return null;
        }
        return zookeeper.create(path, data.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    /**
     * 获取路径下所有子节点
     *
     * @param path 路径
     * @return data
     * @throws KeeperException      ex
     * @throws InterruptedException ex
     */
    public List<String> getChildren(String path) throws KeeperException, InterruptedException {
        List<String> children = zookeeper.getChildren(path, false);
        log.info("获取path={},路径下的所有子节点children={}", path, children);
        return children;
    }

    /**
     * 获取节点上面的数据
     *
     * @param path 路径
     * @return data
     * @throws KeeperException      ex
     * @throws InterruptedException ex
     */
    public String getData(String path) throws KeeperException, InterruptedException {
        byte[] data = zookeeper.getData(path, false, null);
        log.info("获取节点下的数据data={}", new String(data));
        if (data == null) {
            return "";
        }
        return new String(data);
    }

    /**
     * 获取节点上面的数据并设置watch
     *
     * @param path 路径
     * @return data
     * @throws KeeperException      ex
     * @throws InterruptedException ex
     */
    public String getDataSetWatch(String path) throws KeeperException, InterruptedException {
        byte[] data = zookeeper.getData(path, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    //监听节点信息，一旦节点信息发生变化，将通知
                    watchValue(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
        log.info("获取节点下的数据data={}", new String(data));
        if (data == null) {
            return "";
        }
        return new String(data);
    }

    /**
     * 监听节点信息(循环监听节点信息,一旦节点信息发生改变，就收到通知发生相应的变化行为)
     *
     * @param path 路径
     * @return data
     * @throws Exception ex
     */
    public String watchValue(String path) throws Exception {
        byte[] byteArray = zookeeper.getData(path, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    watchValue(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Stat());
        String retValue = new String(byteArray);
        log.info("路径path={},下的节点信息value={}", path, retValue);
        return retValue;
    }

    /**
     * 设置节点信息
     *
     * @param path 路径
     * @param data 数据
     * @return 节点信息
     * @throws KeeperException      ex
     * @throws InterruptedException ex
     */
    public Stat setData(String path, String data) throws KeeperException, InterruptedException {
        Stat stat = zookeeper.setData(path, data.getBytes(), -1);
        log.info("设置节点信息path={},data={},返回的状态信息stat={}", path, data, stat);
        return stat;
    }

    /**
     * 删除节点
     *
     * @param path 路径
     * @throws InterruptedException ex
     * @throws KeeperException      ex
     */
    public void deleteNode(String path) throws InterruptedException, KeeperException {
        zookeeper.delete(path, -1);
    }

    /**
     * 获取节点创建时间
     *
     * @param path 路径
     * @return data
     * @throws KeeperException      ex
     * @throws InterruptedException ex
     */
    public String getCTime(String path) throws KeeperException, InterruptedException {
        Stat stat = zookeeper.exists(path, false);
        log.info("节点路径path={},创建时间ctime={}", path, stat.getCtime());
        return String.valueOf(stat.getCtime());
    }

    /**
     * 获取某个路径下孩子的数量
     *
     * @param path 路径
     * @return data
     * @throws KeeperException      ex
     * @throws InterruptedException ex
     */
    public Integer getChildrenNum(String path) throws KeeperException, InterruptedException {
        int childrenNum = zookeeper.getChildren(path, false).size();
        log.info("节点路径path={},节点路径孩子数量size={}", path, childrenNum);
        return childrenNum;
    }

    /**
     * 判断节点是否存在
     *
     * @param path 节点路径
     * @return data
     * @throws KeeperException      ex
     * @throws InterruptedException ex
     */
    public Boolean isExitZKPath(String path) throws KeeperException, InterruptedException {
        Stat stat = zookeeper.exists(path, false);
        return stat == null;
    }

    /**
     * 关闭连接
     *
     * @throws InterruptedException ex
     */
    public void closeConnection() throws InterruptedException {
        if (zookeeper != null) {
            zookeeper.close();
            log.info("zookeeper close connect");
        }
    }

}