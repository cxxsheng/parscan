### Mac OS 
Turn off SIP or

https://github.com/Z3Prover/z3/issues/294
https://blog.csdn.net/lunarych/article/details/107835522

put JNI dynamic link libraries in: /Library/Java/Extensions
e.g.libz3java.dylib 

put none-JNI dynamic link libraries in: /usr/local/lib
e.g. libz3.dylib

### windows
put libz3.dll and libz3java.dill in $java.library.path 

### ubuntu

```shell
git clone https://github.com/Z3Prover/z3.git -b z3-4.8.17
cd z3/; python scripts/mk_make.py --java
cd build; make
sudo make install
```
