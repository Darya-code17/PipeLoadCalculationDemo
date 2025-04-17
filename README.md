## Pipes' Load Calculation (Demo)

### Project Description

This demo project helps determine whether a given set of pipes can fit inside a truck's cargo compartment.  
The user enters the pipe dimensions (diameter and length) along with the size of the cargo container.  
The application then checks if the pipes can fit, considering **telescoping**, which allows them to nest inside each other to save space.

### Key Features

- **Input Pipe Parameters:** Users enter the necessary specifications for the pipes (e.g., diameter, length, etc.).
- **Define Cargo Compartment Dimensions:** The dimensions of the truck’s cargo area are provided.
- **Load Calculation:** The program determines if the given set of pipes will fit in the specified container.
- **Telescoping Visualization:** A visual demonstration shows how the pipes can be nested inside each other to efficiently utilize space.

### Technologies Used

- **Java 21** – Primary development environment.
- **JTS Topology Suite** - Used for geometric calculations, including collision detection
- **Maven** – Dependency management and build tool.
- **JavaFX** – Handles both UI and visualization
- **CSS** – Used for visual representation of pipes inside the container, ensuring clear and structured display.
- **FXML** – Used for defining UI structure.
    
### Installation and Running

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/Darya-code17/PipesLoadCalculationDemo.git
   ```

2. **Navigate to the Project Directory:**

   ```bash
   cd PipesLoadCalculationDemo
   ```

3. **Build the Project Using Maven Wrapper (no need to install Maven separately):**

   ```bash
   ./mvnw clean compile
   ```

4. **Run the Application:**

   ```bash
   ./mvnw exec:java
   ```
   (For Windows users, use mvnw.cmd exec:java.)

### Alternative Running Method  

If you are using **IntelliJ IDEA**, open the project and run the application using the "Run" button.

### How to Use

After starting the application, simply follow the on-screen instructions to enter the pipe parameters and cargo compartment dimensions. The program will then calculate and visually demonstrate the nesting (telescoping) of the pipes, showing whether they fit into the designated container.

### Limitations  
These features are **currently in development** and will be **available in the full version**:  
- Multi-container support is planned for the commercial version.  
- The ability to specify the quantity of identical pipes in a single entry is under development.  
- Persistent data storage will be introduced, ensuring information is saved after closing the application.  

### Contact

If you need to contact me regarding this project or my work, you can reach me here:
- **LinkedIn:** www.linkedin.com/in/daria-kiriazova
- **Email:** subbotinawork01@gmail.com
