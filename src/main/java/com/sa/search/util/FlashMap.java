package com.sa.search.util;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


public final class FlashMap {
	
	static final String FLASH_MAP_ATTRIBUTE = FlashMap.class.getName();
		
	private FlashMap() {
	}

	public static void setInfoMessage(String info, RedirectAttributes redirect) {
		redirect.addFlashAttribute(MESSAGE_KEY, new Message(MessageType.info, info));
	}

	public static void setWarningMessage(String warning, RedirectAttributes redirect) {
		redirect.addFlashAttribute(MESSAGE_KEY, new Message(MessageType.warning, warning));
	}

	public static void setErrorMessage(String error, RedirectAttributes redirect) {
		redirect.addFlashAttribute(MESSAGE_KEY, new Message(MessageType.error, error));
	}

	public static void setSuccessMessage(String success, RedirectAttributes redirect) {
		redirect.addFlashAttribute(MESSAGE_KEY, new Message(MessageType.success, success));
	}
	
	public static void setSuccessMessage(String success, String arguments,  RedirectAttributes redirect) {
		redirect.addFlashAttribute(MESSAGE_KEY, new Message(MessageType.success, success, arguments));
	}

	private static final String MESSAGE_KEY = "flashMessage";

	public static final class Message {
		
		private final MessageType type;
		
		private final String text;
		
		private String arguments;

		public Message(MessageType type, String text) {
			this.type = type;
			this.text = text;
		}
		
		public Message(MessageType type, String text, String arguments ) {
			this.type = type;
			this.text = text;
			this.arguments = arguments;
		}

		public MessageType getType() {
			return type;
		}

		public String getText() {
			return text;
		}
		
		public String getArguments() {
			return arguments;
		}
		
		public String toString() {
			return type + ": " + text;
		}
	
	}
	
	public static enum MessageType {
		info, success, warning, error
	}
	
}