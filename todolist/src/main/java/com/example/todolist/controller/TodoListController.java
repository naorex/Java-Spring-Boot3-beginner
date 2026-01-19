package com.example.todolist.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import com.example.todolist.entity.Todo;
import com.example.todolist.form.TodoData;
import com.example.todolist.form.TodoQuery;
import com.example.todolist.repository.TodoRepository;
import com.example.todolist.service.TodoService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class TodoListController {

  // @Autowired は省略可のため省略
  private final TodoRepository todoRepository;
  private final TodoService todoService;
  private final HttpSession session;

  @GetMapping("/todo")
  public ModelAndView showTodoList(ModelAndView mv) {
    // 一覧を検索して表示する
    mv.setViewName("todoList");
    List<Todo> todoList = todoRepository.findAll(); // .findAll() は SQL の "SELECT * FROM todo" に相当
    mv.addObject("todoList", todoList);
    mv.addObject("todoQuery", new TodoQuery());
    return mv;
  }

  // ToDo 入力フォーム表示
  // todoList.html で新規追加がクリックされたとき
  @GetMapping("/todo/create")
  public ModelAndView createTodo(ModelAndView mv) {
    mv.setViewName("todoForm");
    mv.addObject("todoData", new TodoData());
    session.setAttribute("mode", "create");
    return mv;
  }

  // todoForm.html で登録がクリックされたとき
  // 上述の createTodo() のオーバーロード
  @PostMapping("/todo/create")
  public String createTodo(
      @ModelAttribute @Validated TodoData todoData,
      BindingResult result, // todoData のチェック結果が格納される
      ModelAndView mv) {

    // エラーチェック
    boolean isValid = todoService.isValid(todoData, result);
    if (!result.hasErrors() && isValid) {
      // エラーなし
      Todo todo = todoData.toEntity();
      todoRepository.saveAndFlush(todo); // SQL の INSERT文に相当
      return "redirect:/todo";
    } else {
      // エラーあり
      return "todoForm";
    }
  }

  // ToDo入力画面でキャンセル登録がクリックされたとき
  @PostMapping("/todo/cancel")
  public String cancel() {
    return "redirect:/todo";
  }

  // リクエストを受け取る
  @GetMapping("/todo/{id}")
  public ModelAndView todoById(
      @PathVariable(name = "id") int id,
      ModelAndView mv) {
    mv.setViewName("todoForm");
    Todo todo = todoRepository.findById(id).get();
    mv.addObject("todoData", todo);
    session.setAttribute("mode", "update");
    return mv;
  }

  @PostMapping("/todo/update")
  public String updateTodo(@ModelAttribute @Validated TodoData todoData,
      BindingResult result,
      Model model) {

    // エラーチェック
    boolean isValid = todoService.isValid(todoData, result);
    if (!result.hasErrors() && isValid) {
      // エラーなし
      Todo todo = todoData.toEntity();
      todoRepository.saveAndFlush(todo);
      return "redirect:/todo";
    } else {
      // エラーあり
      return "todoForm";
    }
  }

  @PostMapping("/todo/delete")
  public String deleteTodo(@ModelAttribute TodoData todoData) {
    todoRepository.deleteById(todoData.getId());
    return "redirect:/todo";
  }

  @PostMapping("/todo/query")
  public ModelAndView queryTodo(
      @ModelAttribute TodoQuery todoQuery,
      BindingResult result,
      ModelAndView mv) {
    mv.setViewName("todoList");
    List<Todo> todoList = null;
    if (todoService.isValid(todoQuery, result)) {
      // エラーが無ければ検索
      todoList = todoService.doQuery(todoQuery);
    }
    mv.addObject("todoList", todoList);
    return mv;
  }
}
