package me.mantou.animator

import me.mantou.animator.render.ButtonRender
import me.mantou.animator.shader.Shader
import me.mantou.animator.util.Interpolators
import org.joml.Matrix2f
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20C.glUseProgram
import org.lwjgl.system.MemoryUtil.NULL

private var window = -1L
private val buttonRender = ButtonRender()

private var lastFrameTime = 0.0
private var deltaTime = 0.0f

fun main() {
    initWindow()
    GL.createCapabilities()

    initRender()

    val buttonShader = genShader("button.vert", "button.frag")

    var buttonSize = 0f
    val buttonAnimation = Animation(
        1000L,
        { p ->
            buttonSize = p
        },
        Interpolators.elastic(),
        repeatMode = Animation.RepeatMode.REVERSE,
        repeatCount = Animation.INFINITE
    )


    lastFrameTime = glfwGetTime()
    while (!glfwWindowShouldClose(window)) {
        glfwPollEvents()

        val currentTime = glfwGetTime()
        deltaTime = (currentTime - lastFrameTime).toFloat()
        lastFrameTime = currentTime

        buttonAnimation.update((deltaTime * 1000).toLong())

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        buttonShader.use()
        buttonShader.setMatrix4f("u_ModelMat", Matrix4f().scale(buttonSize, buttonSize, 1f))
        buttonShader.setVec3("u_Color", 1.0f, 1.0f, 1.0f)

        buttonRender.render()
        glUseProgram(0)

        glfwSwapBuffers(window)
    }
}

fun genShader(vert: String, frag: String): Shader{
    val classLoader = Thread.currentThread().contextClassLoader

    val vertStream = classLoader.getResourceAsStream("assets/shaders/$vert")
        ?: throw RuntimeException("Vertex shader not found: $vert")

    val fragStream = classLoader.getResourceAsStream("assets/shaders/$frag")
        ?: throw RuntimeException("Fragment shader not found: $frag")

    return Shader(vertStream, fragStream)
}

fun initRender(){
    buttonRender.init()
}

fun initWindow() {
    GLFWErrorCallback.createPrint(System.err).set()
    if (!glfwInit()) throw IllegalStateException ("Unable to initialize GLFW")
    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    window = glfwCreateWindow(640, 640, "HelloAnimator", NULL, NULL)
    if (window == NULL) throw RuntimeException("Failed to create the GLFW window")
    glfwSetFramebufferSizeCallback(window) { _, width, height ->
        glViewport(0, 0, width, height)
    }
    glfwMakeContextCurrent(window)
}
