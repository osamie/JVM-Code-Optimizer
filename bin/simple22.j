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
   ifne dummy_label
   ;goto false_label
false_label:
   ldc "false"
   ldc 0
   ifne dummy_label
   ;ldc 2
   ;ldc 3
   ldc 6.0
   ldc 2
   fadd
   
   ldc 2
   ldc 2
   ldc 2
   ;ldc 2
   fdiv
   
   fload 7
  ldc 8.0
  fmul
  fload 2
  ldc 5.0
  fadd
  fmul
	pop
   
cipher_label:
	ldc "table"
	astore  
dummy_label:
    
	;pop
	ldc 0	
	fload 2.0
	ldc 3
	fmul
	;goto test0


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



