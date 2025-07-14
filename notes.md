# My notes


# Switches
    The switch can thow an exception directly after the ->
    If you dont use a -> to yeild a result for your case, you must use this format with either a break or a yeild
    
    switch (type) {
        case 0: 
            var output = String("This is the output for case 0");
            yeild output;
        case 1:
            System.out.println("This is for case 1");
            break;
    }

# What is need for move calculations:
    - Each chessPiece will have its own move patters on what is allowed
        - Rook: can move from a relative position of (0,0) to {(x = 0,0<y<8) and (0<x<8,y = 0)} (Moves to any postion in a strait vertical or horizontal line)
        - Bishop: Can move from a relative position of (0,0) to {(1,1),(2,2),(3,3),(4,4),(-1,-1),(-2,-2),(-3,-3),(-4,-4),
        (1,-1),(2,-2),(3,-3),(4,-4),(-1,1),(-2,2),(-3,3),(-4,4)} (12 total positions from center of board) {(x = i, y = i), (x = -i, y = i), (x = i, y = -i)}
        - Knight: Can move from a relative postion of (0,0) to {(-1,2),(1,2),(-2,1),(2,1),(-2,-1),(2,-1),(-1,-2),(-1,2)} (8 total positions) {(x = )}
        - King: can move to any position in a 1 block radius of current square on board {(x<=1, y<=1)}
        - Queen: can move to any position on the board in a strait line {(x = 0, y < 8), (x < 8, y = 0), (x = i, y = i), (x = -i, y = i), (x = i, y = -i)}
        - Pawn: can move one block forward and attack diagonal. Can move forward 2 on first move. { if first move {(x = 2, y = 0)} else if 
        on attack {(x = 1, y = 1), (x = 1, y = -1)} else {(x = 1, y = 0)}}

# Using Interfaces:
    - Interfaces are a way to group functions together and provide a template for them
    - They allow you to treat all class implementations of the interface the same
    - You can use an interface and have it used by a base class. The base class can then have children class that 
    have their own implementation of base class without having to change how they are used up front.
    Using the interface allows you to build up you program without having to edit every detail. 
    @ in the case of the chess program, I used an interface to estabish that all calculators will have a single method,
    and then I used a base class to provide all the shared code between the calculators. That way, all the calculators 
    acted the same, even though thier implementaitons might be different on the backend. 

# Java I/O:
    4 Ways to Read/ Write files:
        - Steams: Read or write a file sequentially (most common)
            - Binary-Formatted:
                - InputSteam: Interface used to read bytes sequentially from a data source
                    -Example usage:
                        - FileInputStream
                        - PipedInputSteam
                        - URLConnection.getInputStream()
                        - HttpExchange.getRequestBody()
                        - ResultSet.getBinaryStream(int columnIndex)
                        - ect.
                    Filter Input Streams:
                        - A way to perform actions on an input stream.
                        - How it works:
                            - Open an InputStream on a data source, then wrap it in one or more filter input streams 
                            that provide the features you want. 
                        - example: 

                        public void compressFile(String inputFilePath, String outputFilePath) throws IOException {
                            File inputFile = new File(inputFilePath);
                            File outputFile = new File(ouputFilePath);

                            try(FileInputStream fis = new FileInputStream(inputFile);
                                BufferedInputStream bis = new BufferedInputStream(fis); # The buffer acts as a way to limit the amount of time we have to access the file. Make it more efficient. 
                                FileOutputStream fos = new FileOutputStream(outputFile);
                                BufferedOutputStream bos = new Buffered OutputStream(fos);
                                GZipOutputStream zipos = new GZipOutputStream(bos)){ # Here the GZIPOutputStream is the wrapper for the buffered output stream which will 
                                                                                                # apply the zip to the stream as it is written. 

                                byte [] chunk = new byte[CHUNK_SIZE];
                                int bytesRead;
                                while((bytesRead = bis.read(chunk)) > 0) {
                                    zipos.write(chunk, 0 , bytesRead);
                                }
                            }
                        }
                                
                - OutputStream:
                    -Example usage:
                        - FileInputStream
                        - PipedInputSteam
                          - URLConnection.getInputStream()
                          - HttpExchange.getRequestBody()
                          - ResultSet.getBinaryStream(int columnIndex)
                          - ect.
                - Filter Input Streams:
                    - A way to perform actions on an input stream.
                      - How it works:
                      - Open an InputStream on a data source, then wrap it in one or more filter input streams
                      that provide the features you want.
                    - example:


                - Text-Formatted Data:
                    - Reader
                    - Writer
            - Scanner class : Tokenize stream input to read one token at a time
                    import java.io.File;
                    import java.io.FileNotFoundException;
                    import java.util.Scanner;

                    public class ReadFile {
                        public static void main(String[] args) throws FileNotFoundException {
                            File file = new File(args[0]);
                            try (Scanner scanner = new Scanner(file)) {
                                while (scanner.hasNext()) {
                                    System.out.println(scanner.next());
                                }
                            }
                        }
                    }
            - Flies Class: Used to create, delete, copy, ect. whole files. Cant read file.
                - Check file existence : 
                    File file = new file("file path");
                    if(file.exists()) {}
                - Create a file : 
                    File file = new file("file path");
                    file.createNewFile();
                - Delete a file : 
                    File file = new file("file path");
                    file.delete();
            - RandomAccessFile Class : Use a file pointer to read from / write to any location in a file


# Json and reading Json files:
    - Json is a file format that is used to store and transfer data. It can be read in 3 ways:
        - DOM (Document Object Model} Parsers : converst the JSON text to an in memory tree data structure. You can then traverse the tree to
            extract the data. 
        
    - Stream Parsers: Tokenizers that return one token at a time from the JSON data file.
        - great for sorting and extracting data by thier tokens.
        - GSON library is a useful resource here
    - Serializers/ Deserializers
        - Use a library to convert from JSON to Java Objects (and vice versa)


# Phase 3 authentication problems:
Currently any user can register then login after registering even after they are registered and logged in. The problem 
I see with this is that they are able to get multiple authorization tokens which would make it really hard to log them
out by deleting the authToken. I need a way to store the authToken with its associated AuthData. Because the AuthData  
isnt sent in the request, I cant associate the username without puting the authToken as the key to my HashMap. If I do 
this, I cant see if a user has already been logged in by looking to see if their username already has an associated 
authToken. The only way I see being able to do this is if I have two hashMaps - one that stores the token as the key, the 
other stores the username as the key. That way I can access the authData using either the username or token. Seems messy. 