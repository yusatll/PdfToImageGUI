# PDF to Image Converter

A Java-based desktop application that converts PDF files to images (PNG/JPEG) with a user-friendly graphical interface.

## Features

- Convert PDF files to PNG or JPEG images
- Batch conversion of all pages in a PDF
- High-quality output (300 DPI)
- User-friendly graphical interface
- Progress tracking with status updates
- Automatic output directory creation
- Detailed conversion logs

## Requirements

- Java 11 or higher
- Apache PDFBox 3.0.0
- Maven for dependency management

## Installation

1. Clone this repository or download the source code
2. Make sure you have Maven installed
3. Navigate to the project directory
4. Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.0</version>
</dependency>
```

5. Build the project using Maven:

```bash
mvn clean install
```

## Usage

1. Run the application:
   - Through your IDE by running `PdfConverterGUI.java`
   - Or by running the compiled JAR file

2. Using the application:
   - Click "Browse" to select your input PDF file
   - Choose an output directory where images will be saved
   - Select your preferred image format (PNG or JPEG)
   - Click "Convert" to start the conversion process
   - Monitor the progress through the progress bar and log messages

3. Output:
   - Images will be saved in a subfolder named after your PDF file
   - Each page will be saved as a separate image file
   - File naming format: `page_001.png`, `page_002.png`, etc.

## Features in Detail

### Input Selection
- Supports any valid PDF file
- File browser with PDF filter for easy selection

### Output Options
- Choose any directory for output
- Automatic creation of subdirectories
- Organized output structure

### Image Format Options
- PNG format for high quality and lossless compression
- JPEG format for smaller file sizes

### Progress Tracking
- Real-time progress bar
- Detailed log messages for each step
- Clear status indicators

## Technical Details

- Built with Java Swing for the GUI
- Uses Apache PDFBox for PDF processing
- Implements SwingWorker for background processing
- 300 DPI output resolution for high quality
- Error handling and logging

## Error Handling

The application includes comprehensive error handling for common scenarios:
- Invalid PDF files
- Input/Output errors
- File access permissions
- Memory limitations

## Contributing

Feel free to contribute to this project by:
1. Forking the repository
2. Creating a feature branch
3. Committing your changes
4. Pushing to the branch
5. Creating a new Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Apache PDFBox team for their excellent PDF processing library
- Java Swing for providing the GUI framework

## Contact

If you have any questions or suggestions, please feel free to open an issue in the repository.
