import math
import time
import pygame
import sys

#defining global variables/tuples here:
G = 6.67408*(10**-11)

BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
GREY = (200, 200, 200)
RED = (255, 000, 000)
BLUE = (000, 000, 255)
GREEN = (000, 255, 000)
ORANGE = (255, 165, 000)


class Mass:

    def __init__(self, name, mass, radius, parent, orbital_radius, true_anomaly=0, orbital_velo=None):
        #characteristics about the planetary mass
        self.name = name
        self.mass = mass
        self.radius = radius

        self.parent = parent #planet which the mass orbits around
        self.orbital_radius = orbital_radius #initial distance to the parent planet
        self.orbital_velo = orbital_velo
        self.true_anomaly = true_anomaly #initial placement of the planet using polar coordinates

        self.lead_positions = []
        self.lead_velocities = []

    def __repr__(self):
        return self.name

    def planetGravity(self, time_step, i):
        deltax = self.lead_positions[i][0] - self.parent.lead_positions[i][0]
        deltay = self.lead_positions[i][1] - self.parent.lead_positions[i][1]
        distance = math.sqrt((deltax**2) + (deltay**2))
        #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
        force_gravity = ((G*self.mass*self.parent.mass) / (1000*distance)**2) / 1000
        #all angles drawn from the planet
        theta = math.atan2(deltay, deltax)

        xaccel = (force_gravity * -math.cos(theta)) / self.mass
        yaccel = (force_gravity * -math.sin(theta)) / self.mass

        #calculates the change in velocity over a given time step interval
        xvelo = self.lead_velocities[i][0] + (xaccel * time_step) 
        yvelo = self.lead_velocities[i][1] + (yaccel * time_step) 

        self.lead_velocities.append((xvelo, yvelo))

        #determines the change in position over the same time step interval
        xpos = self.lead_positions[i][0] + (xvelo * time_step)
        ypos = self.lead_positions[i][1] + (yvelo * time_step)

        self.lead_positions.append((xpos, ypos))


    def drawPlanet(self):
        if self.name == "Earth":
            pass

    
class SpaceCraft:

    def __init__(self, name, mass, length, parent, orbital_radius, true_anomaly=0, orbital_velo=None):
        self.name = name
        self.mass = mass
        self.length = length
        
        self.parent = parent #initial planet which the mass orbits around
        self.orbital_radius = orbital_radius #initial distance to the parent planet
        self.orbital_velo = orbital_velo 
        self.true_anomaly = true_anomaly #initial placement of the planet using polar coordinates

        self.lead_positions = []
        self.lead_velocities = []

        self.fuel_remaining = 1000

    def __repr__(self):
        return self.name

    def craftGravity(self, planet_list, time_step, i):
        gravity_vectors = []
        for planet in planet_list:
        
            deltax = self.lead_positions[i][0] - planet.lead_positions[i][0]
            deltay = self.lead_positions[i][1] - planet.lead_positions[i][1]
            distance = math.sqrt((deltax**2) + (deltay**2))
            #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
            force_gravity = ((G*craft.mass*planet.mass) / (1000*distance)**2) / 1000
            theta = math.atan2(deltay, deltax)
            gravity_vectors.append((force_gravity, theta))

        net_xforce, net_yforce = 0, 0
        for vector in gravity_vectors:
            
            net_xforce += vector[0] * -math.cos(vector[1])
            net_yforce += vector[0] * -math.sin(vector[1])

        net_xaccel = net_xforce / self.mass
        net_yaccel = net_yforce / self.mass

        ##calculates the change in velocity over a given time step interval
        xvelo = self.lead_velocities[i][0] + (net_xaccel * time_step)
        yvelo = self.lead_velocities[i][1] + (net_yaccel * time_step)	

        self.lead_velocities.append((xvelo, yvelo))

        #determines the change in position over the same time step interval
        xpos = self.lead_positions[i][0] + (xvelo * time_step)
        ypos = self.lead_positions[i][1] + (yvelo * time_step)

        self.lead_positions.append((xpos, ypos))


    def drawCraft(self, screen, position, SCALE_FACTOR):
        ### input display x-y positions along with a scale factor
        #calculates the measurements of the craft given its total length
        xpos, ypos = position[0], position[1]
        tank_length = (1/(SCALE_FACTOR/200)) * self.length * (2/3)
        tank_width = tank_length / 3
        tank_diagonal = math.sqrt(tank_length**2 + tank_width**2)
        tail_diagonal = math.sqrt((tank_length)**2 + (2*tank_width)**2)
        
        #determines the direction of the craft based on its velocity
        nose_direction = math.atan2(self.yvelo, self.xvelo)
        nose_pos = (xpos + math.cos(nose_direction)*(tank_length*2), ypos + math.sin(nose_direction)*(tank_length*2))

        #p1-4: four edges of the tank in counterclockwise order starting from top right (when the craft is pointed up)
        p1_angle = nose_direction - math.atan2(tank_width, tank_length)
        p2_angle = nose_direction + math.atan2(tank_width, tank_length)
        p3_angle = p1_angle + math.pi
        p4_angle = p2_angle - math.pi
        
        #craft is drawn relative using polar coordinates relative to its display center
        p1_pos = (xpos + math.cos(p1_angle)*(tank_diagonal), ypos + math.sin(p1_angle)*(tank_diagonal))
        p2_pos = (xpos + math.cos(p2_angle)*(tank_diagonal), ypos + math.sin(p2_angle)*(tank_diagonal))
        p3_pos = (xpos + math.cos(p3_angle)*(tank_diagonal), ypos + math.sin(p3_angle)*(tank_diagonal))
        p4_pos = (xpos + math.cos(p4_angle)*(tank_diagonal), ypos + math.sin(p4_angle)*(tank_diagonal))

        #all points are collected 
        tank_points = (p1_pos, p2_pos, p3_pos, p4_pos)
        nose_points = (p1_pos, p2_pos, nose_pos)
        right_tail_points = (
            (xpos + math.cos(nose_direction - math.pi/2)*(tank_width), ypos + math.sin(nose_direction - math.pi/2)*(tank_width)), 
            (xpos + math.cos(nose_direction - 2.5)*(tail_diagonal), ypos + math.sin(nose_direction - 2.5)*(tail_diagonal)), 
            p4_pos
        )
        left_tail_points = (
            (xpos + math.cos(nose_direction + math.pi/2)*(tank_width), ypos + math.sin(nose_direction + math.pi/2)*(tank_width)), 
            (xpos + math.cos(nose_direction + 2.5)*(tail_diagonal), ypos + math.sin(nose_direction + 2.5)*(tail_diagonal)), 
            p3_pos
        )
        #each part of the craft is drawn as separate polygons
        pygame.draw.polygon(screen, RED, nose_points)
        pygame.draw.polygon(screen, RED, right_tail_points)
        pygame.draw.polygon(screen, RED, left_tail_points)
        pygame.draw.polygon(screen, GREY, tank_points)
        #pygame.draw.line(screen, ORANGE, (xpos, ypos), (xpos+tank_length*10*math.cos(nose_direction), ypos+tank_length*10*math.sin(nose_direction)))




#GLOBAL FUNCTIONS          
def scale_converter(real_distance, SCALE_FACTOR):
    scaled_distance = real_distance / SCALE_FACTOR
    return round(scaled_distance)












#initialize relative center of the simulation and any planets/moons below it
#Planetary Mass: name, mass, radius, parent_planet, orbital_radius
#sun = Mass("Sun", 1988500*(10**24), 20000, None, 0)
#mercury = Mass("Mercury", 0.330*(10**24), 2439, sun, 57.9*(10**5))
#venus = Mass("Venus", 4.87*(10**24), 6052, sun, 108.2*(10**5))
earth = Mass("Earth", 5.97*(10**24), 6378, None, 149.6*(10**6))
moon = Mass("Moon", 0.073*(10**24), 1737, earth, 0.384*(10**6))


planet_list = [earth, moon]

#SpaceCraft: name, mass, length, initial_parent, orbital_radius, true_anomaly=0, orbital_velo=None
#if no value inputed for initial velocity, it will assume a circular orbit
craft = SpaceCraft("craft", 10, 3, earth, 7878, 0)


#parameters: display dimensions, simulation width included in the display window, iteration pause interval
def main(screen_width, screen_height, simulation_width, physics_fps, lead_length):

    #display infomation and dimensions
    DP_WIDTH, DP_HEIGHT = screen_width, screen_height    
    REAL_WIDTH = simulation_width #raw width of the display in km
    SCALE_FACTOR = round(REAL_WIDTH / DP_WIDTH) #ratio of km:1 pixel
    TARGET = craft #either a class object or x-y coordinates
    
    multiplier_options = (1, 10, 100, 1000, 10000)
    time_multiplier = multiplier_options[0]
    time_step = time_multiplier / physics_fps

    #craft will always have the -1 index
    body_list = []
    for planet in planet_list:
        body_list.append(planet)
    body_list.append(craft)
    
    #sets up the initial conditions of the universe relative to some point/planet
    relative_center = None
    for planet in planet_list:
        if planet.parent == None:
            relative_center = planet
            planet.xpos, planet.ypos = 0, 0
            planet.xvelo, planet.yvelo = 0, 0
            break

    #places each body in orbits around their parent planets, everything of which is relative to the center
    for body in body_list:
        #relative center will stay unmoving at the origin of the simulation
        if body == relative_center:
            continue
        #converts polar coordinates into x-y cartesian coordinates and assigns them as an attribute of each body
        body.xpos = body.orbital_radius*math.cos(body.true_anomaly) + body.parent.xpos
        body.ypos = body.orbital_radius*math.sin(body.true_anomaly) + body.parent.ypos

        #converts tangential orbital velocities into x-y velocities and assigns them as an attribute of each body
        if body.orbital_velo == None:
            orbital_velo = -1*math.sqrt((G*body.parent.mass)/(1000*body.orbital_radius)) / 1000 #convert m/s to km/s
        body.xvelo = orbital_velo*math.cos(body.true_anomaly+math.pi/2) + body.parent.xvelo
        body.yvelo = orbital_velo*math.sin(body.true_anomaly+math.pi/2) + body.parent.yvelo

    #initializes planet and craft position/velocity lists with values adjusted relative to the center
    for body in body_list:
        body.lead_positions = [(body.xpos, body.ypos)]
        body.lead_velocities = [(body.xvelo, body.yvelo)]

    #boolean determining whether to recalculate the lead
    recalculate_lead = True
    multiplier_change = 0

    #initializes pygame library tools and display window
    pygame.init()
    screen = pygame.display.set_mode((DP_WIDTH, DP_HEIGHT))
    
    counter = 0 #iteration counter to help understand the speed of each iteration
    #event loop
    running = True
    while running:
        start = time.time()
        # input_start = time.time()
        counter += 1

        #pygame.event list keeps track of all keypress/mouseclicks that occur
        for event in pygame.event.get():
            
            #closes window when exit button pressed
            if event.type == pygame.QUIT:
                running = False

            if event.type == pygame.KEYDOWN:
                
                #zoom in or out using the 1 and 2 number keys, respectively
                if event.key == pygame.K_1:
                    SCALE_FACTOR = round(0.5 * SCALE_FACTOR, 5)
                if event.key == pygame.K_2:
                    SCALE_FACTOR = round(1.5 * SCALE_FACTOR, 5)
                #switch the target object
                if event.key == pygame.K_q:
                    if body_list.index(TARGET)+1 == len(body_list):
                        TARGET = body_list[0]
                    else:
                        TARGET = body_list[body_list.index(TARGET)+1]
                if event.key == pygame.K_e:
                    if body_list.index(TARGET)+1 == len(body_list):
                        TARGET = body_list[0]
                    else:
                        TARGET = body_list[body_list.index(TARGET)-1]

                #change the simulation speed
                if event.key == pygame.K_z:
                    if multiplier_options.index(time_multiplier)+1 != len(multiplier_options):
                        old_multiplier = time_multiplier
                        time_multiplier = multiplier_options[multiplier_options.index(time_multiplier)+1]
                        time_step = time_multiplier / physics_fps
                        
                        multiplier_change = round(time_multiplier / old_multiplier)
                        adjustment_left = round(lead_length / multiplier_change)

                if event.key == pygame.K_x:
                    if multiplier_options.index(time_multiplier) > 0:
                        time_multiplier = multiplier_options[multiplier_options.index(time_multiplier)-1]
                        time_step = time_multiplier / physics_fps
                

                # if event.key == pygame.K_w or event.key == pygame.K_s:
                #     thrust_change = True
                #     #forward/prograde: 1    backwards/retrograde: -1
                #     if event.key == pygame.K_w:
                #         direction = 1
                #     else:
                #         direction = -1
                #     if craft.fuel_remaining > 0:
                #         craft_velo = math.sqrt(craft.xvelo**2 + craft.yvelo**2)
                #         craft_direction = math.atan2(craft.yvelo, craft.xvelo)

                #         thrust_x = (direction*0.01*craft_velo)*math.cos(craft_direction)
                #         thrust_y = (direction*0.01*craft_velo)*math.sin(craft_direction)

                #         craft.fuel_remaining -= 1
                #     else:
                #         print("no fuel remaining!!!")
                # else:
                #     thrust_x, thrust_y = 0, 0
        
        # input_stop = time.time()
        # grav_start = time.time()

        #creates a list of future planet/craft positions
        if recalculate_lead == True:

            for i in range(lead_length-1):

                for planet in planet_list:

                    if planet == relative_center:
                        planet.lead_positions.append((0, 0))
                        planet.lead_velocities.append((0, 0))
                        continue

                    planet.planetGravity(time_step, i)

                craft.craftGravity(planet_list, time_step, i)

        elif multiplier_change != 0:
            
            for i in range(multiplier_change):

                for planet in planet_list:

                    if planet == relative_center:
                        planet.lead_positions.append((0, 0))
                        planet.lead_velocities.append((0, 0))
                        continue

                    planet.planetGravity(time_step, -1)

                craft.craftGravity(planet_list, time_step, -1)
            
        else:

            for planet in planet_list:

                if planet == relative_center:
                        planet.lead_positions.append((0, 0))
                        planet.lead_velocities.append((0, 0))
                        continue

                planet.planetGravity(time_step, -1)
            
            craft.craftGravity(planet_list, time_step, -1)
            

        for body in body_list:
            body.xpos, body.ypos = body.lead_positions[0][0], body.lead_positions[0][1]
            body.xvelo, body.yvelo = body.lead_velocities[0][0], body.lead_velocities[0][1]

        # grav_stop = time.time()
        # dp_start = time.time()

        DP_CENTER_X = DP_WIDTH/2 + (-1 * scale_converter(TARGET.xpos, SCALE_FACTOR))
        DP_CENTER_Y = DP_HEIGHT/2 + (-1 * scale_converter(TARGET.ypos, SCALE_FACTOR))

        screen.fill(BLACK)
        #draw planets
        for planet in planet_list:
            dp_position = (DP_CENTER_X + scale_converter(planet.xpos, SCALE_FACTOR), DP_CENTER_Y + scale_converter(planet.ypos, SCALE_FACTOR))
            pygame.draw.circle(screen, WHITE, dp_position, scale_converter(planet.radius, SCALE_FACTOR))
            pygame.draw.polygon(screen, ORANGE, [dp_position, [dp_position[0]+5, dp_position[1]-10], [dp_position[0]-5, dp_position[1]-10]])
        
        #draw craft
        craft_dp_pos = (DP_CENTER_X + scale_converter(craft.xpos, SCALE_FACTOR), DP_CENTER_Y + scale_converter(craft.ypos, SCALE_FACTOR))
        craft.drawCraft(screen, craft_dp_pos, SCALE_FACTOR)
        #draw the craft's future path
        for i in range(lead_length-1):
            dp_pos = (DP_CENTER_X + scale_converter(craft.lead_positions[i][0], SCALE_FACTOR), DP_CENTER_Y + scale_converter(craft.lead_positions[i][1], SCALE_FACTOR))
            pygame.draw.circle(screen, BLUE, dp_pos, 1)
        #craft triangle marker
        pygame.draw.polygon(screen, GREEN, (craft_dp_pos, (craft_dp_pos[0]+5, craft_dp_pos[1]-10), (craft_dp_pos[0]-5, craft_dp_pos[1]-10)))

        if multiplier_change != 0:
            for i in range(multiplier_change):
                for body in body_list:
                    body.lead_positions.pop(0)
                    body.lead_velocities.pop(0)

            adjustment_left -= 1
            if adjustment_left <= 0:
                multiplier_change = 0

        else:
            for body in body_list:
                body.lead_positions.pop(0)
                body.lead_velocities.pop(0)

        recalculate_lead = False

        pygame.display.set_caption(str(counter))
        pygame.display.update()

        finish = time.time()
        # dp_stop = time.time()

        iteration_length = finish - start

        # print(f"""
        # Input: {input_stop-input_start}
        # Grav: {grav_stop-grav_start}
        # Display: {dp_stop-dp_start}
        # total: {iteration_length}

        # """)

        iteration_pause = (1/physics_fps) - iteration_length
        if iteration_pause < 0:
            iteration_pause = 0
        time.sleep(iteration_pause)

#parameters: screen_width, screen_height, simulation_width, physics_fps, dp_fps, lead_length
main(1000, 1000, 1000000, 240, 500)