package com.example.app.batch;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.List;

public class ProcessorItemReader<T> implements ItemReader<T> {
    private List<T> data; // Replace with your data source, e.g., database query, file reader, etc.
    private int index = 0;

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (index < data.size()) {
            return data.get(index++);
        } else {
            return null;
        }
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void open(ExecutionContext executionContext) {
        // Optionally, you can implement custom logic to open and initialize resources.
    }

    public void update(ExecutionContext executionContext) {
        // Optionally, you can implement custom logic to update execution context.
    }

    public void close() {
        // Optionally, you can implement custom logic to close resources.
    }
}
