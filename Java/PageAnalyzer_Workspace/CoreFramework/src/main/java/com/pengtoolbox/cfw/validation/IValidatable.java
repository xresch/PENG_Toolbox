package com.pengtoolbox.cfw.validation;

public interface IValidatable<T> {
	
	public abstract boolean validate();
	public abstract IValidatable<T> setName(String propertyName);
	public abstract String getName();
	public abstract boolean setValue(T value);
	public abstract T getValue();

	public IValidatable<T> addValidator(IValidator e);

	public boolean removeValidator(IValidator o);
	
}
