; Begin standard header
.class public simple
.super java/lang/Object
.method public <init>()V
  aload_0

  invokenonvirtual java/lang/Object/<init>()V
  return
.end method
.method public static main([Ljava/lang/String;)V
  invokestatic simple/main()F
  return
.end method
; End standard header


.method public static main()F
.limit stack 50
.limit locals 50
 ldc 0.0
  fstore 0   ; x
  ldc 1
  invokestatic simple/nottest(I)I
  invokestatic simple/println(I)I
  pop
  fload 0   ;x
  freturn
.end method
; Begin standard trailer

.method public static print(F)F
   .limit stack 2
   .limit locals 1
   getstatic java/lang/System/out Ljava/io/PrintStream;
   fload 0
   invokestatic java/lang/Float/toString(F)Ljava/lang/String;
   invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
   fload 0
   freturn
.end method

.method public static print(Ljava/lang/String;)Ljava/lang/String;
   .limit stack 2
   .limit locals 2
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload 0
   invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
   aload 0
   areturn
.end method

.method public static print(I)I
   .limit stack 5
   .limit locals 2
   getstatic java/lang/System/out Ljava/io/PrintStream;
   iload 0
   ifeq false_label
   ldc "true"
   goto print_it
false_label:
   ldc "false"
print_it:
   invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
   iload 0
   ireturn
.end method


.method public static println(F)F
   .limit stack 2
   .limit locals 1
   getstatic java/lang/System/out Ljava/io/PrintStream;
   fload 0
   invokestatic java/lang/Float/toString(F)Ljava/lang/String;
   invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
   fload 0
   freturn
.end method

.method public static println(Ljava/lang/String;)Ljava/lang/String;
   .limit stack 2
   .limit locals 2
   getstatic java/lang/System/out Ljava/io/PrintStream;
   aload 0
   invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
   aload 0
   areturn
.end method

.method public static println(I)I
   .limit stack 5
   .limit locals 2
   getstatic java/lang/System/out Ljava/io/PrintStream;
   iload 0
   ifeq false_label
   ldc "true"
   goto print_it
false_label:
   ldc "false"
print_it:
   invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
   iload 0
   ireturn
.end method

.method public static pow(FF)F
   .limit stack 5
   .limit locals 2
   fload 0
   f2d
   fload 1
   f2d
   invokestatic java/lang/Math/pow(DD)D
   d2f
   freturn
.end method
.method public static cos(F)F
	.limit stack 5
	.limit locals 2
	fload 0
	f2d
	invokestatic java/lang/Math/cos(D)D
	d2f
	freturn
.end method
.method public static sin(F)F
	.limit stack 5
	.limit locals 2
	fload 0
	f2d
	invokestatic java/lang/Math/sin(D)D
	d2f
	freturn
.end method
.method public static concat(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
	.limit stack 20
	.limit locals 20
	new java/lang/StringBuffer
	dup
	invokespecial java/lang/StringBuffer/<init>()V
	aload 0
	invokevirtual java/lang/StringBuffer/append(Ljava/lang/String;)Ljava/lang/StringBuffer;
	aload 1
	invokevirtual java/lang/StringBuffer/append(Ljava/lang/String;)Ljava/lang/StringBuffer;
	invokevirtual java/lang/StringBuffer/toString()Ljava/lang/String;
	areturn

.end method
.method public static concat(Ljava/lang/String;I)Ljava/lang/String;
	.limit stack 20
	.limit locals 20
	new java/lang/StringBuffer
	dup
	invokespecial java/lang/StringBuffer/<init>()V
	aload 0
	invokevirtual java/lang/StringBuffer/append(Ljava/lang/String;)Ljava/lang/StringBuffer;
	iload 1
   ifeq retFalse
	;pop 
	ldc "true "
	goto end
	retFalse:
	;pop
	ldc "false "
	end:
	invokevirtual java/lang/StringBuffer/append(Ljava/lang/String;)Ljava/lang/StringBuffer;
	invokevirtual java/lang/StringBuffer/toString()Ljava/lang/String;
	areturn

.end method
.method public static concat(ILjava/lang/String;)Ljava/lang/String;
	.limit stack 20
	.limit locals 20
	new java/lang/StringBuffer
	dup
	invokespecial java/lang/StringBuffer/<init>()V
	iload 0
	
   ifeq retFalse
	;pop 
	ldc "true "
	goto end
	retFalse:
	;pop
	ldc "false "
	end:
	invokevirtual java/lang/StringBuffer/append(Ljava/lang/String;)Ljava/lang/StringBuffer;
	aload 1
	invokevirtual java/lang/StringBuffer/append(Ljava/lang/String;)Ljava/lang/StringBuffer;
	invokevirtual java/lang/StringBuffer/toString()Ljava/lang/String;
	areturn

.end method
; End of standard trailer
