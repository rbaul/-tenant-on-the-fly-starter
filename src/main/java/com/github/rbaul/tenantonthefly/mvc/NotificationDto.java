package com.github.rbaul.tenantonthefly.mvc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto<T> {

	private NotificationTypeDto notificationType;
	
	private T content;
	
}