package renderers;

public abstract class Renderer {
	
	protected ShaderProgram program;
	
	public Renderer(ShaderProgram program) {
		this.program = program;
	}
	
	public void cleanUp() {
		program.cleanUp();
	}
	
	public ShaderProgram getProgram() {
		return program;
	}
	
}
