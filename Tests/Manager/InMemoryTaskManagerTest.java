package Manager;

import Manager.InMemoryTaskManager;
import Model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
        setManager(new InMemoryTaskManager());
    }

    @AfterEach
    public void after() {
        Task.setCount(0);
    }

}