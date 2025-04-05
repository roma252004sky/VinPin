package vinpin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.vinpin.WebController;

@SpringBootTest(classes = {WebController.class})
@AutoConfigureMockMvc
public class MainApplicationTest {
    @Autowired
    private MockMvc mvc;
    @Test
    void testController() throws Exception {
        mvc.perform(get("/main?name=Test"))
                .andExpect(status().isOk());
    }
}