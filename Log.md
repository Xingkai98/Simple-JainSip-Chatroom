## Log

20190619

- [x] 增加“重置”按钮的功能。见JCheckBox。
- [x] 增加“Enter”键发送消息的功能。见KeyListener相关。
- [x] 聊天内容更新为ScrollPanel。
- [x] 修正“必须重新点击窗体才会更新在线列表“

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

