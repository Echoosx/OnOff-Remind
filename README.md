# OnOff-Remind

> 基于 [Mirai Console](https://github.com/mamoe/mirai-console) 的Bot上线和离线自定义消息插件

[![Release](https://img.shields.io/github/v/release/Echoosx/OnOff-Remind)](https://github.com/Echoosx/OnOff-Remind/releases)
[![Build](https://github.com/Echoosx/OnOff-Remind/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)](https://github.com/Echoosx/OnOff-Remind/actions/workflows/gradle.yml)

## 指令
注意: 使用前请确保可以 [在聊天环境执行指令](https://github.com/project-mirai/chat-command)  
带括号的`/`前缀是缺省的  
`<...>`中的是指令名，由`空格`隔开表示其中任一名称都可执行  
`[...]`表示参数，当`[...]`后面带`?`时表示参数可选  
`{...}`表示连续的多个参数


| 指令                              | 描述                                  |
|:--------------------------------|:------------------------------------|
| `/<remind> [on\off]`            | 交互式添加Bot上线和下线`通知消息`                 |
| `/<remind> <add> [contact] [on\off]?` | 添加通知对象`contact`,`on\off`缺省时上下线通知都添加 |
| `/<remind> <remove> [contact] [on\off]?` | 移除通知对象`contact`,`on\off`缺省时上下线通知都移除 |
| `/<remind> <list>` | 列出当前Bot上线和下线所有的`通知对象`               |

注意：
- 参数`contact`为通知对象的`id`，可以使用前缀`g123456`和`u123456`的方式区别`群组`与`用户`，不使用前缀则自动查找符合id的对象
- 想要接收离线通知请使用`/<stop>`指令关闭mirai-console，非正常关闭不能触发离线通知

### 示例
```
# 给群123456增加上线通知
/remind add g123456 on

# 移除用户123456的离线通知
/remind remove u123456 off

# 给用户或群组123456增加上线和离线通知
/remind add 123456
```


## 安装
- 从 [Releases](https://github.com/Echoosx/OnOff-Remind/releases) 下载`jar`包，将其放入工作目录下`plugins`文件夹
- 如果没有`plugins`文件夹，先运行 [Mirai Console](https://github.com/mamoe/mirai-console) ，会自动生成
