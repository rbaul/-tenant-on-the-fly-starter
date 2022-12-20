package com.github.rbaul.tenantonthefly.mvc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class TenantNotificationDto extends NotificationDto<TenantDto> {
	
}