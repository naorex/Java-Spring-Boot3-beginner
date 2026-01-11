package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor // 全フィールドへ値をセットするコンストラクタ
@Getter // フィールドに対する getter メソッド
public class History {
  private int seq;
  private int yourAnswer;
  private String result;
}
