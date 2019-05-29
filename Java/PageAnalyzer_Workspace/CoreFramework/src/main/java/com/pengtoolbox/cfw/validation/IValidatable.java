package com.pengtoolbox.cfw.validation;

public interface IValidatable<T> {
	
	public abstract boolean validateValue(Object value);
	public abstract IValidatable<T> setPropertyName(String propertyName);
	public abstract String getPropertyName();
	public abstract IValidatable<T> setValue(T value);
	public abstract T getValue();

	public boolean addValidator(IValidator e);

	public boolean removeValidator(IValidator o);
}
