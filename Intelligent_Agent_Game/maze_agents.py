import random
from collections import deque
import heapq
import tkinter as tk

# ======================
# Parameters
# ======================
ROWS, COLS = 15, 15
CELL_SIZE = 30
WALL_PROB = 0.3

# Moves
MOVES = [(-1, 0), (1, 0), (0, -1), (0, 1)]

# ======================
# Maze Generation
# ======================

def generate_maze():
    return [[0 if random.random() > WALL_PROB else 1 for _ in range(COLS)] for _ in range(ROWS)]

maze = generate_maze()

free_cells = [(r, c) for r in range(ROWS) for c in range(COLS) if maze[r][c] == 0]
start = random.choice(free_cells)
GOAL = random.choice(free_cells)


def is_valid(pos):
    r, c = pos
    return 0 <= r < ROWS and 0 <= c < COLS and maze[r][c] == 0

# ======================
# Reflex Agent
# ======================

def reflex_agent(start):
    current = start
    visited = set()
    path = [current]

    while current != GOAL:
        visited.add(current)
        neighbors = []

        for dr, dc in MOVES:
            nxt = (current[0] + dr, current[1] + dc)
            if is_valid(nxt) and nxt not in visited:
                neighbors.append(nxt)

        if not neighbors:
            return None

        current = random.choice(neighbors)
        path.append(current)

    return path

# ======================
# Model-Based Agent (DFS)
# ======================

def model_based_agent(start):
    visited = set()
    stack = [(start, [start])]

    while stack:
        current, path = stack.pop()

        if current in visited:
            continue

        visited.add(current)

        if current == GOAL:
            return path

        for dr, dc in MOVES:
            nxt = (current[0] + dr, current[1] + dc)
            if is_valid(nxt):
                stack.append((nxt, path + [nxt]))

    return None

# ======================
# BFS
# ======================

def bfs(start):
    queue = deque([start])
    parent = {start: None}

    while queue:
        current = queue.popleft()

        if current == GOAL:
            break

        for dr, dc in MOVES:
            nxt = (current[0] + dr, current[1] + dc)
            if is_valid(nxt) and nxt not in parent:
                parent[nxt] = current
                queue.append(nxt)

    if GOAL not in parent:
        return None

    path = []
    node = GOAL
    while node:
        path.append(node)
        node = parent[node]

    return path[::-1]

# ======================
# A*
# ======================

def heuristic(a, b):
    return abs(a[0] - b[0]) + abs(a[1] - b[1])


def a_star(start):
    open_list = []
    heapq.heappush(open_list, (0, start))

    g_cost = {start: 0}
    parent = {start: None}

    while open_list:
        _, current = heapq.heappop(open_list)

        if current == GOAL:
            break

        for dr, dc in MOVES:
            nxt = (current[0] + dr, current[1] + dc)

            if not is_valid(nxt):
                continue

            new_cost = g_cost[current] + 1

            if nxt not in g_cost or new_cost < g_cost[nxt]:
                g_cost[nxt] = new_cost
                f_cost = new_cost + heuristic(nxt, GOAL)
                heapq.heappush(open_list, (f_cost, nxt))
                parent[nxt] = current

    if GOAL not in parent:
        return None

    path = []
    node = GOAL
    while node:
        path.append(node)
        node = parent[node]

    return path[::-1]

# ======================
# GUI (Tkinter)
# ======================

class MazeApp:
    def __init__(self, root):
        self.root = root
        self.root.title("Maze Solver")

        main_frame = tk.Frame(root)
        main_frame.pack()

        # Canvas (left)
        self.canvas = tk.Canvas(main_frame, width=COLS * CELL_SIZE, height=ROWS * CELL_SIZE)
        self.canvas.pack(side=tk.LEFT)

        # Sidebar (right)
        sidebar = tk.Frame(main_frame, padx=10)
        sidebar.pack(side=tk.RIGHT, fill=tk.Y)

        tk.Label(sidebar, text="Results", font=("Arial", 12, "bold")).pack()

        self.result_text = tk.Text(sidebar, width=30, height=25)
        self.result_text.pack()

        # Buttons
        frame = tk.Frame(root)
        frame.pack()

        tk.Button(frame, text="Generate Maze", command=self.reset).pack(side=tk.LEFT)
        tk.Button(frame, text="Reflex Agent", command=self.solve_reflex).pack(side=tk.LEFT)
        tk.Button(frame, text="Model-Based", command=self.solve_model).pack(side=tk.LEFT)
        tk.Button(frame, text="Solve BFS", command=self.solve_bfs).pack(side=tk.LEFT)
        tk.Button(frame, text="Solve A*", command=self.solve_astar).pack(side=tk.LEFT)

        self.draw_maze()

    def draw_maze(self, path=None):
        self.canvas.delete("all")

        for r in range(ROWS):
            for c in range(COLS):
                x1, y1 = c * CELL_SIZE, r * CELL_SIZE
                x2, y2 = x1 + CELL_SIZE, y1 + CELL_SIZE

                color = "white" if maze[r][c] == 0 else "black"
                self.canvas.create_rectangle(x1, y1, x2, y2, fill=color, outline="gray")

        if path:
            for (r, c) in path:
                x1, y1 = c * CELL_SIZE, r * CELL_SIZE
                x2, y2 = x1 + CELL_SIZE, y1 + CELL_SIZE
                self.canvas.create_rectangle(x1, y1, x2, y2, fill="blue")

        # start and goal
        r, c = start
        self.canvas.create_rectangle(c * CELL_SIZE, r * CELL_SIZE,
                                     c * CELL_SIZE + CELL_SIZE, r * CELL_SIZE + CELL_SIZE,
                                     fill="green")

        r, c = GOAL
        self.canvas.create_rectangle(c * CELL_SIZE, r * CELL_SIZE,
                                     c * CELL_SIZE + CELL_SIZE, r * CELL_SIZE + CELL_SIZE,
                                     fill="red")

    def update_sidebar(self, name, path):
        self.result_text.insert(tk.END, f"\n{name}:\n")
        if path:
            self.result_text.insert(tk.END, f"Length: {len(path)}\nPath: {path}\n")
        else:
            self.result_text.insert(tk.END, "No path found\n")

    def reset(self):
        global maze, start, GOAL
        maze = generate_maze()
        free_cells = [(r, c) for r in range(ROWS) for c in range(COLS) if maze[r][c] == 0]
        start = random.choice(free_cells)
        GOAL = random.choice(free_cells)
        self.draw_maze()
        self.result_text.delete(1.0, tk.END)

    def solve_reflex(self):
        path = reflex_agent(start)
        self.draw_maze(path)
        self.update_sidebar("Reflex Agent", path)

    def solve_model(self):
        path = model_based_agent(start)
        self.draw_maze(path)
        self.update_sidebar("Model-Based Agent", path)

    def solve_bfs(self):
        path = bfs(start)
        self.draw_maze(path)
        self.update_sidebar("BFS", path)

    def solve_astar(self):
        path = a_star(start)
        self.draw_maze(path)
        self.update_sidebar("A*", path)

# ======================
# Run App
# ======================

if __name__ == "__main__":
    root = tk.Tk()
    app = MazeApp(root)
    root.mainloop()
    root = tk.Tk()
    app = MazeApp(root)
    root.mainloop()
