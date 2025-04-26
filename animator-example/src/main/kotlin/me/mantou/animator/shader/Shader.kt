package me.mantou.animator.shader

import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20C.*
import org.lwjgl.opengl.GL32C.GL_GEOMETRY_SHADER
import org.lwjgl.system.MemoryUtil
import java.io.InputStream

class Shader(
    vert: InputStream,
    frag: InputStream,
    geom: InputStream? = null
) {
    private var programID = 0

    init {
        val vertID = compileShader(vert, GL_VERTEX_SHADER)
        val fragID = compileShader(frag, GL_FRAGMENT_SHADER)
        val geomID = geom?.let { compileShader(it, GL_GEOMETRY_SHADER) }

        programID = glCreateProgram().also {
            glAttachShader(it, vertID)
            glAttachShader(it, fragID)
            geomID?.let { id -> glAttachShader(programID, id) }
            glLinkProgram(it)
            checkProgramLinkStatus(it)
        }

        glDeleteShader(vertID)
        glDeleteShader(fragID)
        geomID?.let { glDeleteShader(it) }
    }

    fun use() {
        glUseProgram(programID)
    }

    fun destroy() {
        glDeleteProgram(programID)
    }

    fun setBool(name: String, value: Boolean) {
        glUniform1i(glGetUniformLocation(programID, name), if (value) 1 else 0)
    }

    fun setInt(name: String, value: Int) {
        glUniform1i(glGetUniformLocation(programID, name), value)
    }

    fun setFloat(name: String, value: Float) {
        glUniform1f(glGetUniformLocation(programID, name), value)
    }

    fun setMatrix4f(name: String, value: Matrix4f) {
        val floatBuffer = BufferUtils.createFloatBuffer(4 * 4)
        value.get(floatBuffer)

        glUniformMatrix4fv(
            glGetUniformLocation(programID, name),
            false,
            floatBuffer
        )
    }

    fun setVec3(name: String, x: Float, y: Float, z: Float) {
        glUniform3f(glGetUniformLocation(programID, name), x, y, z)
    }

    fun setVec2(name: String, x: Float, y: Float) {
        glUniform2f(glGetUniformLocation(programID, name), x, y)
    }

    private fun checkProgramLinkStatus(programID: Int) {
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            val errorLog = glGetProgramInfoLog(programID)
            glDeleteProgram(programID)
            throw RuntimeException("Shader program linking failed:\n$errorLog")
        }
    }

    companion object {
        fun compileShader(inputStream: InputStream, shaderType: Int): Int {
            val shaderCode = inputStream.bufferedReader().use { it.readText() }
            val shaderID = glCreateShader(shaderType)

            glShaderSource(shaderID, shaderCode)
            glCompileShader(shaderID)

            if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
                val errorLog = glGetShaderInfoLog(shaderID)
                glDeleteShader(shaderID)
                throw RuntimeException("Shader compilation failed:\n$errorLog")
            }

            return shaderID
        }
    }
}