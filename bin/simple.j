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
  fstore 0   ; i
  ldc 5.0
  fstore 0   ; i
  fload 0   ;i
  ldc 2.0
  fmul
  ldc 2.0
  fmul
  ldc 2.0
  fmul
  pop
  ldc 0.0
  fstore 1   ; x
  ldc 8.0
  fstore 1   ; x
  ldc 2.0
  ldc 2.0
  ;fmul
  fload 1   ;x
  fmul
  pop
  ldc 0.0
  freturn
.end method
; Begin standard trailer
