package org.appxi.property;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class RawProperty<T> {
    private T value;

    private List<BiConsumer<T, T>> listeners = null;

    private RawProperty<T> binding;

    public RawProperty() {
        this(null);
    }

    public RawProperty(T value) {
        this.value = value;
    }

    public T get() {
        // 如果存在绑定的目标对象，则直接返回目标值
        if (null != this.binding) {
            return this.binding.get();
        }
        return this.value;
    }

    public RawProperty<T> set(T value) {
        // 如果存在绑定的目标对象，则不支持修改当前值
        if (null != this.binding) {
            return this;
        }
        if (value != this.value) {
            T oldValue = this.value;
            this.value = value;
            // 通知监听器当前值已变化
            notifyListeners(oldValue, value);
        }
        //
        return this;
    }

    public RawProperty<T> addListener(BiConsumer<T, T> valueListener) {
        if (null == this.listeners) {
            this.listeners = new ArrayList<>();
        }
        this.listeners.add(valueListener);
        //
        return this;
    }

    public RawProperty<T> removeListener(BiConsumer<T, T> valueListener) {
        if (null != this.listeners) {
            this.listeners.remove(valueListener);
        }
        //
        return this;
    }

    public RawProperty<T> clearListeners() {
        if (null != this.listeners) {
            this.listeners.clear();
        }
        //
        return this;
    }

    private void notifyListeners(T oldValue, T newValue) {
        if (Objects.equals(oldValue, newValue)) {
            return;
        }
        if (null != this.listeners) {
            for (BiConsumer<T, T> valueListener : this.listeners) {
                valueListener.accept(oldValue, newValue);
            }
        }
    }

    public boolean isBound() {
        return null != this.binding;
    }

    public RawProperty<T> bind(RawProperty<T> target) {
        Objects.requireNonNull(target);

        T oldValue = null != this.binding ? this.binding.get() : this.value;
        this.binding = target;
        // 通知监听器当前值已变化
        notifyListeners(oldValue, target.get());
        return this;
    }

    public RawProperty<T> unbind() {
        if (null != this.binding) {
            T oldValue = this.binding.get();
            this.binding = null;
            // 通知监听器当前值已变化
            notifyListeners(oldValue, this.value);
        }
        //
        return this;
    }
}
