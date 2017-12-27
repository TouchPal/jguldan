# Guldan Java客户端

## 如何使用
在你的maven依赖里面，需要添加下面这项

```xml
        <dependency>
            <groupId>com.cootek.datainfra</groupId>
            <artifactId>jguldan</artifactId>
            <version>1.0-RELEASE</version>
            <type>pom</type>
        </dependency>
```

## 代码示例

```java
    public static void main(String[] args) {
        GuldanClient guldanClient = new GuldanClient("http://127.0.0.1:5000");
        guldanClient.addValidator("test", "test", "test", new IConfigValidator() {
            @Override
            public void validate(String config) throws Throwable {
                if (!config.contains("alex")) {
                    throw new Exception("notcontainalex");
                }
            }
        });
        guldanClient.subscribeChanges("test", "test", "test", new AbstractConfigChangeListener() {
            @Override
            public void onChange(String configName, String config) {
                super.onChange(configName, config);
                System.out.println("alex " + configName + " changedto " + config);
            }
        });
        while (true) {
            try {
                String config = guldanClient.getPublicConfig("test", "test", "test");
                System.out.println(config);
            } catch (Throwable t) {
                System.out.println(t);
            } finally {
                GuldanUtils.sleep(1);
            }
        }
    }

```
