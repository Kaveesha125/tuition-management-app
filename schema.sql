-- Tuition Management System - SQL Schema
-- Database: PostgreSQL
-- Description: Schema for managing users, students, teachers, courses, attendance, assignments, fees, and more.
-- Author: [Your Name]
-- Note: Adjust data types as needed for other SQL dialects.

-- =====================================
-- 1. USERS
-- =====================================
CREATE TABLE public.user (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255),
  role VARCHAR(20), -- 'admin', 'teacher', 'student'
  is_verified BOOLEAN DEFAULT FALSE
);

-- =====================================
-- 2. COURSES
-- =====================================
CREATE TABLE public.courses (
  course_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  course_name VARCHAR(100),
  description VARCHAR(255)
);

-- =====================================
-- 3. STUDENTS
-- =====================================
CREATE TABLE public.Students (
  student_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  user_id BIGINT NOT NULL,
  course_id BIGINT,
  parent_contact NUMERIC,
  CONSTRAINT Students_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user(id),
  CONSTRAINT Students_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id)
);

-- =====================================
-- 4. TEACHERS
-- =====================================
CREATE TABLE public.teachers (
  teacher_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  user_id BIGINT,
  course_id BIGINT,
  CONSTRAINT teachers_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user(id),
  CONSTRAINT teachers_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id)
);

-- =====================================
-- 5. ASSIGNMENTS
-- =====================================
CREATE TABLE public.assignments (
  assignments_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  course_id BIGINT,
  teacher_id BIGINT,
  title VARCHAR(150),
  description VARCHAR(255),
  due_date DATE,
  upload_url VARCHAR(255),
  CONSTRAINT assignments_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id),
  CONSTRAINT assignments_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.teachers(teacher_id)
);

-- =====================================
-- 6. SUBMISSIONS
-- =====================================
CREATE TABLE public.submissions (
  submissions_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  assignment_id BIGINT,
  student_id BIGINT,
  submission_url VARCHAR(255),
  submitted_at DATE,
  CONSTRAINT submissions_assignment_id_fkey FOREIGN KEY (assignment_id) REFERENCES public.assignments(assignments_id),
  CONSTRAINT submissions_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.Students(student_id)
);

-- =====================================
-- 7. ATTENDANCE
-- =====================================
CREATE TABLE public.attendance (
  attendance_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  student_id BIGINT,
  teacher_id BIGINT,
  course_id BIGINT,
  date DATE,
  status VARCHAR(20), -- 'present', 'absent', etc.
  CONSTRAINT attendance_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.Students(student_id),
  CONSTRAINT attendance_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.teachers(teacher_id),
  CONSTRAINT attendance_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id)
);

-- =====================================
-- 8. FEES
-- =====================================
CREATE TABLE public.fees (
  fee_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  student_id BIGINT,
  course_id BIGINT,
  amount NUMERIC,
  month VARCHAR(20),
  status VARCHAR(20), -- 'paid', 'unpaid'
  receipt_url VARCHAR(255),
  CONSTRAINT fees_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.Students(student_id),
  CONSTRAINT fees_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id)
);

-- =====================================
-- 9. MATERIALS
-- =====================================
CREATE TABLE public.materials (
  material_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  course_id BIGINT,
  teacher_id BIGINT,
  title VARCHAR(150),
  file_url VARCHAR(255),
  CONSTRAINT materials_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id),
  CONSTRAINT materials_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.teachers(teacher_id)
);

-- =====================================
-- 10. RESULTS
-- =====================================
CREATE TABLE public.results (
  results_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  student_id BIGINT,
  course_id BIGINT,
  teacher_id BIGINT,
  score NUMERIC,
  graded_at DATE,
  CONSTRAINT results_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.Students(student_id),
  CONSTRAINT results_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(course_id),
  CONSTRAINT results_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.teachers(teacher_id)
);

-- =====================================
-- 11. NOTIFICATIONS
-- =====================================
CREATE TABLE public.notifications (
  notification_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  sent_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  sender_id BIGINT,
  receiver_id BIGINT,
  message VARCHAR(255),
  type VARCHAR(50),
  is_read BOOLEAN DEFAULT FALSE,
  CONSTRAINT notifications_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.user(id),
  CONSTRAINT notifications_receiver_id_fkey FOREIGN KEY (receiver_id) REFERENCES public.user(id)
);
