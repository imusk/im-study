
## 生成证书

#### 使用keytool生成证书
这个命令一般在JDK\jre\lib\security\目录下操作

keytool常用命令

参数 | 释义
----|----
-alias  |  证书的别名
-keystore  |  证书库的名称
-storepass  |  证书库的密码
-keypass  |  证书的密码
-list  |  显示密钥库中的证书信息
-v  |  显示密钥库中的证书详细信息
-export  |  显示密钥库中的证书信息
-file  |  指定导出证书的文件名和路径
-delete  |  删除密钥库中某条目
-import  |  将已签名数字证书导入密钥库
-keypasswd  |  修改密钥库中指定条目口令
-dname  |  指定证书拥有者信息
-keyalg  |  指定密钥的算法
-validity  |  指定创建的证书有效期多少天
-keysize  |  指定密钥长度


#### 具体生成证书操作
1. 创建服务端秘钥
    ```bash
    keytool -genkey -alias 别名-nettyServer -keysize 1024 -validity 3650 -keyalg RSA -dname "CN=localhost" -keypass 证书密码 -storepass 服务端的证书仓库密码 -keystore serverCerts.jks
    ```

2. 导出服务端秘钥
    ```bash
    keytool -export -alias 别名-nettyServer -keystore serverCerts.jks -storepass 服务端的证书仓库密码 -file serverCert.cer
    ```

3. 创建客户端秘钥
    ```bash
    keytool -genkey -alias 别名-nettyClient -keysize 1024 -validity 3650 -keyalg RSA -dname "CN=PF,OU=YJC,O=YJC,L=BJ,S=BJ,C=ZN" -keypass 证书密码 -storepass 客户端的证书仓库密码 -keystore clientCerts.jks
    ```

4. 导出客户端秘钥
    ```bash
    keytool -export -alias 别名-nettyClient -keystore clientCerts.jks -file nettyclientCert.cer -storepass 客户端的证书仓库密码
    ```

5. 将客户端的证书导入到服务端的信任证书仓库中
    ```bash
    keytool -import -trustcacerts -alias 别名-smcc -file nettyClientCert.cer -storepass 服务端的证书仓库密码 -keystore serverCerts.jks
    ```

6. 将服务端的证书导入到客户端的信任证书仓库中
    ```bash
    keytool -import -trustcacerts -alias 别名-smccClient -file serverCert.cer -storepass 客户端的证书仓库密码 -keystore clientCerts.jks
    ```



