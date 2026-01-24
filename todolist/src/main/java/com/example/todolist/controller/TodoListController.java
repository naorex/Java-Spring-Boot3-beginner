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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;

import com.example.todolist.dao.TodoDaoImpl;
import com.example.todolist.entity.Todo;
import com.example.todolist.form.TodoData;
import com.example.todolist.form.TodoQuery;
import com.example.todolist.repository.TodoRepository;
import com.example.todolist.service.TodoService;

import jakarta.persistence.PersistenceContext;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TodoListController {

  // @Autowired は省略可のため省略
  private final TodoRepository todoRepository;
  private final TodoService todoService;
  private final HttpSession session;

  @PersistenceContext
  private EntityManager entityManager;
  TodoDaoImpl todoDaoImpl;

  @PostConstruct
  public void init() {
    todoDaoImpl = new TodoDaoImpl(entityManager);
  }

  @GetMapping("/todo")
  public ModelAndView showTodoList(
      ModelAndView mv,
      @PageableDefault(page = 0, size = 5, sort = "id") Pageable pabeable) {
    // 一覧を検索して表示する
    mv.setViewName("todoList");
    Page<Todo> todoPage = todoRepository.findAll(pabeable);
    mv.addObject("todoQuery", new TodoQuery());
    mv.addObject("todoPage", todoPage);
    mv.addObject("todoList", todoPage.getContent());
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
      // todoList = todoDaoImpl.findByJPQL(todoQuery);
      todoList = todoDaoImpl.findByCriteria(todoQuery);
    }
    mv.addObject("todoList", todoList);
    return mv;
  }

  @PostMapping("/todo/query")
  public ModelAndView queryTodo(
      @ModelAttribute TodoQuery todoQuery,
      BindingResult result,
      @PageableDefault(page = 0, size = 5) Pageable pageable,
      ModelAndView mv) {
    mv.setViewName("todoList");

    // session に保存されている条件で検索
    TodoQuery todoQuery = (TodoQuery) session.getAttribute("todoQuery");
    Page<Todo> todoPage = todoDaoImpl.findByCriteria(todoQuery, pageable);
    if (todoService.isValid(todoQuery, result)) {
      // エラーがなければ検索
      todoPage = todoDaoImpl.findByCriteria(todoQuery, pageable);

      // 入力された検索条件を session に保存
      session.setAttribute("todoQuery", todoQuery);

      mv.addObject("todoPage", todoPage);
      mv.addObject("todoList", todoPage.getContent());
    } else {
      // エラーがあった場合検索
      mv.addObject("todoPage", null);
      mv.addObject("todoList", null);
    }
    return mv;
  }
}
