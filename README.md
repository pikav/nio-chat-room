什么是NIO呢？

  官方：非阻塞的I/O模型, 它支持面向缓冲区(Buffer)的、基于通道(Channel)的IO操作，由三大组件组成：缓冲区Buffer、通道Channel、选择器Selector
  
  我的理解： 
  
    selector相当于一个容器类,一个注册中心,存在于服务端，但又是一个服务端与客户端的中转站; 
    
    buffer就是装数据的箱子，可以打开放入，可以打开取出;
    
    Channel就是一个可以双向运转管道, 把装好数据的箱子扔进去, 会自动把箱子运输到另一端
    
    执行过程：
    
      1. 首先在服务端创建一个ServerSocketChannel(服务端通道连接selector的通道),  注册(安装)到selector中, 并且有监听设备(可以监听连接事件、
         可读事件等等)，监听是否有客户端通道的创建注册以及创建好的通道是否有数据流入。
         
      2. 客户端想要与服务端互动，传输数据，首先要自己创建一个socketChannel(客户端与selector连接的通道)，注册(安装)到selector，
         并且与ServerSocketChannel对接。
         
      3. 当客户端创建通道注册到selector，服务端的selector则会触发连接监听事件，执行操作回传数据给客户端
      
      4. 当客户端在创建好的通道中扔入数据，服务端的selector则会触发可读监听事件，获取数据，执行操作
      
      (服务端ServerSocketChannel 与 客户端socketChannel 是一对多的关系, 服务端一个单线程 处理 多个客户端请求)
