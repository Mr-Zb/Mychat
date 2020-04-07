 package com.fun.framework.support;
 
 public final class TokenException extends  RuntimeException
 {
   private Integer code;
   private String info;

   public TokenException(Integer code, String info) {
     this.code = code;
     this.info = info;
   }

   public Integer getCode() {
     return this.code;
   }

   public void setCode(Integer code) {
     this.code = code;
   }

   public String getInfo() {
     return this.info;
   }

   public void setInfo(String info) {
     this.info = info;
   }
 }

