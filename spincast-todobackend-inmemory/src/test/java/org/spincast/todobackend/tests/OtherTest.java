package org.spincast.todobackend.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.spincast.core.validation.IValidationResult;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.todobackend.inmemory.config.AppConstants;
import org.spincast.todobackend.inmemory.models.ITodo;
import org.spincast.todobackend.inmemory.models.Todo;
import org.spincast.todobackend.inmemory.models.validators.TodoValidator;
import org.spincast.todobackend.inmemory.repositories.InMemoryTodoRepository;

import com.google.inject.Inject;

/**
 * Various tests.
 */
public class OtherTest extends AppIntegrationTestBase {

    @Inject
    protected TodoValidator todoValidator;

    /**
     * Test repository.
     */
    protected InMemoryTodoRepository memoryTodoRepository = new InMemoryTodoRepository();

    @Before
    public void clearRepo() {
        this.memoryTodoRepository.deleteAllTodos();
        assert (this.memoryTodoRepository.getAllTodos().size() == 0);
    }

    protected TodoValidator getTodoValidator() {
        return this.todoValidator;
    }

    //==========================================
    // Maximum number of Todos.
    //==========================================
    @Test
    public void maxTodoNbr() throws Exception {

        for(int i = 0; i < AppConstants.MAX_TODOS_NBR; i++) {
            this.memoryTodoRepository.addTodo(new Todo());
        }

        try {
            this.memoryTodoRepository.addTodo(new Todo());
            fail();
        } catch(Exception ex) {
        }

        assertEquals(0, this.memoryTodoRepository.getAllTodos().size());

    }

    //==========================================
    // The maximum id sequence
    //==========================================
    @Test
    public void maxIdSequence() throws Exception {

        Field todoIdsSequenceField = this.memoryTodoRepository.getClass().getDeclaredField("todoIdsSequence");
        assertNotNull(todoIdsSequenceField);

        todoIdsSequenceField.setAccessible(true);
        todoIdsSequenceField.set(this.memoryTodoRepository, Integer.MAX_VALUE - 1);

        ITodo addTodo = this.memoryTodoRepository.addTodo(new Todo());
        assertNotNull(addTodo);
        assertEquals(Integer.valueOf(Integer.MAX_VALUE - 1), addTodo.getId());

        addTodo = this.memoryTodoRepository.addTodo(new Todo());
        assertNotNull(addTodo);
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), addTodo.getId());

        //==========================================
        // Sequence should have been restarted!
        //==========================================
        addTodo = this.memoryTodoRepository.addTodo(new Todo());
        assertNotNull(addTodo);
        assertEquals(Integer.valueOf(1), addTodo.getId());
    }

    //==========================================
    // Title's maximum length (255)
    //==========================================
    @Test
    public void titleMaxLength() throws Exception {

        ITodo todo = new Todo();
        todo.setTitle(StringUtils.repeat("x", 255));

        IValidationResult validationResult = getTodoValidator().validate(todo);
        assertTrue(validationResult.isValid());

        todo.setTitle(StringUtils.repeat("x", 256));

        validationResult = getTodoValidator().validate(todo);
        assertFalse(validationResult.isValid());
    }

}
