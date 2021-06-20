package com.example.study.user.domain;

import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {

	@Id
	private String id;
	private String name;
	private String password;
	private List<String> roles;

	private User() {
	}

	public User(String name, String password, List<String> roles) {
		this.name = name;
		this.password = password;
		this.roles = roles;
	}
}
