package com.pengtoolbox.cfw.validation;

public interface IValidatable<T> {
	
	public abstract boolean validate();
	public abstract IValidatable<T> setPropertyName(String propertyName);
	public abstract String getPropertyName();
	public abstract IValidatable<T> setValue(T value);
	public abstract T getValue();

	public IValidatable<T> addValidator(IValidator e);

	public boolean removeValidator(IValidator o);
	
}
