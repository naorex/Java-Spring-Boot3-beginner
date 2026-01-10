package com.example.demo;

import lombok.Data;

@Data // Lombok の機能で getter/setter、コンストラクタ、toString()等を自動生成
public class RegistData {
  private String name;
  private String password;
  private int gender;
  private int area;
  private int[] interest;
  private String remarks;
}
