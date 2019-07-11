## Log

20190619

- [x] 增加“重置”按钮的功能。见JCheckBox。
- [x] 增加“Enter”键发送消息的功能。见KeyListener相关。
- [x] 聊天内容更新为ScrollPanel。
- [x] 修正“必须重新点击窗体才会更新在线列表“

20190708

- [x] 私聊消息提示“xxx悄悄地对你说”
- [x] 在线列表改为ScrollPanel

## Notes

#### KeyListener相关

KeyListener需要实现的方法有三个：

- KeyPressed
- KeyReleased
- KeyTyped

这里，一个容易搞混的区别是：前两者识别的是Virtual Key，而KeyTyped接受的是字符的ASCII码。前两者的方法只能接受到“用户按了某个按键”这个操作。也就是说，只有KeyTyped才能获取到实际所按按键的ASCII码。

例如，用户摁下Shift+'A'，前两个方法识别到的是用户按到了VK_A，而KeyTyped识别出了用户输入的是'A'。

#### JCheckBox

setSelected方法并不会改变界面的实际勾选效果，只是设置了是否勾选这个参数；如果需要更改界面的实际效果，需要使用doClick方法。

### 必须resize窗口才能刷新组件？

```java
//layout是JFrame实体
layout.revalidate();
layout.repaint();
```

### Mac端Eclipse的神奇字符编码选项

最开始用Mac端Eclipse打开了项目，中文全部为乱码。查看字符编码后发现字符编码为‘utf-8’，但是中文却出现了乱码。在选择eclipse中的默认字符编码时，选项是在下拉菜单中选的，然而选项中没有“GBK”一项。这时我以为没有GBK编码。

后来，我查阅资料后发现，在选择框中输入“GBK”然后点击“Apply”按钮，就行了。所以下拉菜单有啥用？真滴很奇怪