package me.mantou.animator

import imgui.ImGui
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import imgui.type.ImInt
import me.mantou.animator.render.ButtonRender
import me.mantou.animator.shader.Shader
import me.mantou.animator.util.AnimationUtils
import me.mantou.animator.util.Interpolators
import org.joml.Matrix4f
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20C.glUseProgram
import org.lwjgl.system.MemoryUtil.NULL
import java.awt.Color

private var window = -1L
private lateinit var imGuiImplGlfw: ImGuiImplGlfw
private lateinit var imGuiImplGl3: ImGuiImplGl3

private val buttonRender = ButtonRender()

private lateinit var buttonShader: Shader
var buttonSize = 0f
var buttonColor = Color(1f, 1f, 1f, 1f)
val buttonAnimation = Animation(
    1000L,
    { p ->
        buttonSize = p
        buttonColor = AnimationUtils.lerpColor(
            Color(1f, 1f, 1f, 1f),
            Color(0f, 1f, 1f, 1f),
            p
        )
    },
    Interpolators.Linear,
    repeatMode = Animation.RepeatMode.REVERSE,
    repeatCount = Animation.INFINITE
)

private var selectedInterpolator = ImInt(0)
private val interpolators = listOf(
    "Linear" to Interpolators.Linear,
    "Accelerate" to Interpolators.Accelerate,
    "Decelerate" to Interpolators.Decelerate,
    "Overshoot" to Interpolators.overshoot(1.5f),
    "Elastic" to Interpolators.elastic()
)

private var lastFrameTime = 0.0
private var deltaTime = 0.0f

fun main() {
    initWindow()
    GL.createCapabilities()
    initImGui()
    initRender()

    buttonShader = genShader("button.vert", "button.frag")
    lastFrameTime = glfwGetTime()
    while (!glfwWindowShouldClose(window)) {
        val currentTime = glfwGetTime()
        deltaTime = (currentTime - lastFrameTime).toFloat()
        lastFrameTime = currentTime

        buttonAnimation.update((deltaTime * 1000).toLong())

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        buttonShader.use()
        buttonShader.setMatrix4f("u_ModelMat", Matrix4f().scale(buttonSize, buttonSize, 1f))

//        buttonColor = AnimationUtils.hsvColorCyclic(0.01f)
        buttonShader.setVec3("u_Color", buttonColor.red / 255f, buttonColor.green / 255f, buttonColor.blue / 255f)

        buttonRender.render()
        glUseProgram(0)

        renderDebugGui()

        glfwSwapBuffers(window)
        glfwPollEvents()
    }

    destroy()
}

fun genShader(vert: String, frag: String): Shader{
    val classLoader = Thread.currentThread().contextClassLoader

    val vertStream = classLoader.getResourceAsStream("assets/shaders/$vert")
        ?: throw RuntimeException("Vertex shader not found: $vert")

    val fragStream = classLoader.getResourceAsStream("assets/shaders/$frag")
        ?: throw RuntimeException("Fragment shader not found: $frag")

    return Shader(vertStream, fragStream)
}

fun initImGui(){
    ImGui.createContext()
    val io = ImGui.getIO()
    io.iniFilename = null

    imGuiImplGlfw = ImGuiImplGlfw().apply {
        init(window, true)
    }
    imGuiImplGl3 = ImGuiImplGl3().apply {
        init()
    }
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

fun destroy(){
    imGuiImplGlfw.shutdown()
    imGuiImplGl3.shutdown()
    ImGui.destroyContext()

    buttonShader.destroy()
    buttonRender.destroy()

    Callbacks.glfwFreeCallbacks(window)
    glfwDestroyWindow(window)
    glfwTerminate()
}

fun renderDebugGui(){
    imGuiImplGlfw.newFrame()
    imGuiImplGl3.newFrame()
    ImGui.newFrame()
    ImGui.begin("Debug")

    val interpolatorNames = interpolators.map { it.first }.toTypedArray()
    if (ImGui.combo(
            "Interpolator",
            selectedInterpolator,
            interpolatorNames,
            interpolatorNames.size
    )) {
        buttonAnimation.interpolator = interpolators[selectedInterpolator.get()].second
    }

    ImGui.end()
    ImGui.render()
    imGuiImplGl3.renderDrawData(ImGui.getDrawData())
}
