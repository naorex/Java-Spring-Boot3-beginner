package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// メソッドで処理した結果をそのままレスポンスとしてブラウザへ送信
@RestController
public class HelloController {

  @GetMapping("/hello") // GETリクエストを処理するメソッド（ハンドラーメソッド）を意味
  public String sayHello() {
    return "こんにちは！";
  }
}
