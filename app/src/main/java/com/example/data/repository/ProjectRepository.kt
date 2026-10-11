package com.example.data.repository

import com.example.data.local.ProjectDao
import com.example.data.local.SnapshotDao
import com.example.data.model.Project
import com.example.data.model.ProjectSnapshot
import kotlinx.coroutines.flow.Flow

class ProjectRepository(
    private val projectDao: ProjectDao,
    private val snapshotDao: SnapshotDao
) {
    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()

    suspend fun getProjectById(id: Long): Project? = projectDao.getProjectById(id)

    suspend fun insertProject(project: Project): Long = projectDao.insertProject(project)

    suspend fun updateProject(project: Project) = projectDao.updateProject(project)

    suspend fun deleteProject(project: Project) = projectDao.deleteProject(project)

    suspend fun deleteProjectById(id: Long) = projectDao.deleteProjectById(id)

    suspend fun getProjectsCount(): Int = projectDao.getCount()

    fun getSnapshotsForProject(projectId: Long): Flow<List<ProjectSnapshot>> =
        snapshotDao.getSnapshotsForProject(projectId)

    suspend fun createSnapshot(snapshot: ProjectSnapshot): Long =
        snapshotDao.insertSnapshot(snapshot)

    suspend fun deleteSnapshot(snapshot: ProjectSnapshot) =
        snapshotDao.deleteSnapshot(snapshot)
}
