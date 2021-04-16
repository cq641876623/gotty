单selector模式下的多线程异步io
=================
 主要问题：channel.register方法被selector中的锁住publickeys锁住,原因是selector.select()没有返回时则会锁住publickeys，当高并发时会出现accept无法正常完成
 
 多selector多线程
 ----------------- 
 在Test.class中有实现但并不理想
 
 
纯NIO单线程
  -----------------
  效果不错 
  
  看到网上有其他人封装的NIO多线程，最终还是要避免selector.selector()