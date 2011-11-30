; Begin standard header2
.class public simple
.super java/lang/Object
.method public <init>()V
  aload_0

  invokenonvirtual java/lang/Object/<init>()V
  return
.end method
;.method public static main([Ljava/lang/String;)V
 ; invokestatic simple/main()F
  ;return
;.end method
; End standard header


.method public static println(I)I
   .limit stack 5
   .limit locals 2
   getstatic java/lang/System/out Ljava/io/PrintStream;
   iload 0
   ifeq false_label
   ldc "true"
   ;goto print_it
   ifne print_it
   ;goto false_label
false_label:
   ldc "false"
   ifne fi
   
dummy_label:
	pop
	ldc 0
	ifeq fi
	;goto fi
	pop
	ldc "true"
	goto test0


fi:  
	pop
	ldc "false"
	ifne fi
	pop
	iload 2
	;goto print_it
	
bla:
	ldc "anybla"
	astore 
	
tr: 
	pop
	ldc "true"
	goto print_it
	
print_it:
   invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
   iload 0
   ireturn
   
test2: 
	pop
	ldc"wathever"
test0:
	fload 0.0
	fload2.0
	fmul
.end method


.method public static main()F
.limit stack 50
.limit locals 50
 ldc 0.0
  fstore 0   ; x
  ldc "test"
  ;invokestatic simple/print(Ljava/lang/String;)Ljava/lang/String;
  pop
  fload 0   ;x
  freturn
.end method
; Begin standard trailer



