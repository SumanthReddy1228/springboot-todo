package com.sumanth.springboot.todowebapp.todo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.validation.Valid;

//@Controller
//@SessionAttributes("name")
public class TodoController {

	public TodoController(TodoService todoService) {
		super();
		this.todoService = todoService;
	}

	private TodoService todoService;

	@GetMapping("/list-todos")
	public String listAllTodos(ModelMap model) {
		String username = getLoggedinUsername(model);
		List<Todo> todos = todoService.findByUserName(username);
		model.put("todos", todos);
		return "listTodos";
	}

	@GetMapping("/add-todo")
	public String showNewTodoPage(ModelMap model) {
		String username = getLoggedinUsername(model);
		Todo todo = new Todo(0, username, "", LocalDate.now().plusYears(1), false);
		model.put("todo", todo);
		return "todo";
	}

	@PostMapping("/add-todo")
	public String addTodo(ModelMap model, @Valid Todo todo, BindingResult bindingResult) { // todo is mapped to form in
																							// todo.jsp
		if (bindingResult.hasErrors()) {
			return "todo";
		}
		String username = getLoggedinUsername(model);
		todoService.addTodo(username, todo.getDescription(), todo.getTargetDate(), false);
		return "redirect:list-todos";
	}

	@RequestMapping(value = "/delete-todo", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteTodo(@RequestParam int id) {
		todoService.deleteById(id);
		return "redirect:list-todos";
	}

	@GetMapping("/update-todo")
	public String showUpdateTodo(@RequestParam int id, ModelMap modelMap) {
		Todo todo = todoService.findById(id);
		modelMap.addAttribute("todo", todo);
		return "todo";
	}

	@PostMapping("/update-todo")
	public String updateTodo(ModelMap modelMap, @Valid Todo todo, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "todo";
		}
		String username = getLoggedinUsername(modelMap);
		todo.setUsername(username);
		todoService.update(todo);
		return "redirect:list-todos";
	}

	private String getLoggedinUsername(ModelMap model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
}
